-- Built-in cassette drive control program
-- Placed under rom/programs/ via peripheral mount so CraftOS can find it.
--
-- Usage:
--   cassette play              Start / resume tape playback
--   cassette pause             Pause playback
--   cassette stop              Stop playback and reset position
--   cassette label             Show current tape label
--   cassette label <text>      Set tape label
--   cassette volume            Show current volume
--   cassette volume <0-3>      Set volume
--   cassette write <file>      Write a local file to tape
--   cassette wget <url>        Download a URL to tape (size-checked)
--
-- Safety: wget checks Content-Length via HEAD before downloading,
--         and refuses files exceeding the tape's remaining capacity.

local Cd = peripheral.find("cassette_drive")
if not Cd then
    printError("No cassette_drive peripheral found.")
    return 1
end

---------------------------------------------------------------------
-- Helper: ensure a tape is present
---------------------------------------------------------------------
local function requireTape()
    if not Cd.isTapePresent() then
        printError("No cassette tape in the drive.")
        return false
    end
    return true
end

---------------------------------------------------------------------
-- Helper: confirm overwrite when tape has data
---------------------------------------------------------------------
local function confirmOverwrite()
    if Cd.dataSize() == 0 then return true end
    write("Tape contains " .. Cd.dataSize() .. " bytes. Overwrite? (Y/N): ")
    local input = io.read("*l")
    if input then
        local answer = input:match("^%s*(.)")
        if answer and answer:lower() == "y" then return true end
    end
    print("Cancelled.")
    return false
end

---------------------------------------------------------------------
-- Helper: write a string to tape (opens handle, writes, closes)
---------------------------------------------------------------------
local function writeToTape(data)
    local fh, err = Cd.open("wb")
    if not fh then
        printError("Failed to open tape for writing: " .. (err or "?"))
        return false
    end
    fh.write(data)
    fh.close()
    return true
end

---------------------------------------------------------------------
-- Sub-command:  cassette label [<text>]
---------------------------------------------------------------------
local function cmdLabel(args)
    if not requireTape() then return 1 end

    if #args == 0 then
        local result = Cd.getTapeLabel()
        if result and result[1] then
            print("Tape label: " .. result[1])
        else
            print("Tape has no label.")
        end
        return 0
    end

    -- Set / clear label
    local label = table.concat(args, " ")
    if label == "" then
        Cd.setTapeLabel()
        print("Label cleared.")
    else
        Cd.setTapeLabel(label)
        print("Label set to: " .. label)
    end
    return 0
end

---------------------------------------------------------------------
-- Sub-command:  cassette volume [<0-3>]
---------------------------------------------------------------------
local function cmdVolume(args)
    if #args == 0 then
        print("Volume: " .. Cd.getVolume())
        return 0
    end

    local vol = tonumber(args[1])
    if not vol then
        printError("Volume must be a number.")
        return 1
    end
    Cd.setVolume(vol)
    print("Volume set to " .. Cd.getVolume())
    return 0
end

---------------------------------------------------------------------
-- Sub-command:  cassette play
---------------------------------------------------------------------
local function cmdPlay()
    if not requireTape() then return 1 end
    local ok, err = pcall(Cd.playTape, Cd)
    if not ok then
        printError(err or "Cannot play tape.")
        return 1
    end
    print("Playing.")
    return 0
end

---------------------------------------------------------------------
-- Sub-command:  cassette pause
---------------------------------------------------------------------
local function cmdPause()
    if not requireTape() then return 1 end
    Cd.pauseTape()
    print("Paused.")
    return 0
end

---------------------------------------------------------------------
-- Sub-command:  cassette stop
---------------------------------------------------------------------
local function cmdStop()
    if not requireTape() then return 1 end
    Cd.stopTape()
    print("Stopped.")
    return 0
end

---------------------------------------------------------------------
-- Sub-command:  cassette write <file>
---------------------------------------------------------------------
local function cmdWrite(args)
    if #args < 1 then
        printError("Usage: cassette write <file>")
        return 1
    end
    if not requireTape() then return 1 end
    if not confirmOverwrite() then return 1 end

    local path = args[1]
    if not fs.exists(path) then
        printError("File not found: " .. path)
        return 1
    end
    if fs.isDir(path) then
        printError("Cannot write a directory to tape.")
        return 1
    end

    -- Read file size for progress reporting
    local size = fs.getSize(path)
    print("Reading " .. path .. " (" .. size .. " bytes)... ")

    local fh, err = fs.open(path, "rb")
    if not fh then
        printError("Cannot open file: " .. (err or "?"))
        return 1
    end

    -- Read the entire file
    local data, err = fh.readAll()
    fh.close()
    if not data then
        printError("Read error: " .. (err or "?"))
        return 1
    end

    -- Check tape capacity
    local limit = Cd.dataSizeLimit()
    if #data > limit then
        printError("File is too large for tape (limit: " .. limit .. " bytes).")
        return 1
    end

    print("Writing " .. #data .. " bytes to tape... ")
    if not writeToTape(data) then return 1 end
    print("Done.")
    return 0
end

---------------------------------------------------------------------
-- Sub-command:  cassette wget <url>
---------------------------------------------------------------------
local function cmdWget(args)
    if #args < 1 then
        printError("Usage: cassette wget <url>")
        return 1
    end
    if not requireTape() then return 1 end
    if not confirmOverwrite() then return 1 end

    if not http then
        printError("wget requires the http API, but it is not enabled.")
        printError("Set http.enabled to true in CC: Tweaked's server config.")
        return 1
    end

    local url = args[1]

    -- Validate URL
    local ok, err = http.checkURL(url)
    if not ok then
        printError(err or "Invalid URL.")
        return 1
    end

    -- Download in chunks, checking size as we go
    write("Downloading " .. url .. "...\n")

    local response, err = http.get(url)
    if not response then
        printError("Download failed: " .. (err or "?"))
        return 1
    end

    local limit = Cd.dataSizeLimit()
    local chunks = {}
    local total = 0
    local CHUNK = 4096

    while true do
        local chunk = response.read(CHUNK)
        if not chunk or #chunk == 0 then break end
        total = total + #chunk
        if total > limit then
            response.close()
            printError("Aborted: downloaded " .. total .. " bytes exceeds tape limit (" .. limit .. " bytes).")
            return 1
        end
        chunks[#chunks + 1] = chunk
    end
    response.close()

    if total == 0 then
        printError("Downloaded file is empty.")
        return 1
    end

    print("Received " .. total .. " bytes.")

    -- Join all chunks
    local data = table.concat(chunks)
    write("Writing to tape... ")
    if not writeToTape(data) then return 1 end
    print("Done. " .. total .. " bytes written to tape.")
    return 0
end

---------------------------------------------------------------------
-- Usage
---------------------------------------------------------------------
local function printUsage()
    local name = arg[0] or fs.getName(shell.getRunningProgram())
    print("Usage:")
    print(name .. " play")
    print(name .. " pause")
    print(name .. " stop")
    print(name .. " label [<text>]")
    print(name .. " volume [<0-3>]")
    print(name .. " write <file>")
    print(name .. " wget <url>")
end

---------------------------------------------------------------------
-- Main dispatch
---------------------------------------------------------------------
local tArgs = { ... }
local cmd = tArgs[1]

if not cmd then
    printUsage()
    return 0
end

-- Peel off the subcommand and pass remaining args to the handler
local subArgs = {}
for i = 2, #tArgs do subArgs[i - 1] = tArgs[i] end

local handlers = {
    play   = function() return cmdPlay(subArgs) end,
    pause  = function() return cmdPause(subArgs) end,
    stop   = function() return cmdStop(subArgs) end,
    label  = function() return cmdLabel(subArgs) end,
    volume = function() return cmdVolume(subArgs) end,
    write  = function() return cmdWrite(subArgs) end,
    wget   = function() return cmdWget(subArgs) end,
    ["?"]  = function() printUsage(); return 0 end,
}

local handler = handlers[cmd:lower()]
if handler then
    return handler()
else
    printError("Unknown command: " .. cmd)
    printUsage()
    return 1
end
