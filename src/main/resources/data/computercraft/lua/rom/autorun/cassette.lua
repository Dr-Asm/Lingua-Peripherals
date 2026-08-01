-- Shell auto-completion for the `cassette` program.
--
-- This file lives in rom/autorun/, so CraftOS runs it automatically at
-- boot time (after /rom/startup.lua registers the built-in programs'
-- completion functions, before the shell shows its prompt).
--
-- Registering here (rather than inside cassette.lua itself) matches how
-- CC:Tweaked handles completion for its own programs: the completion
-- function must exist before the user ever types `cassette <Tab>`, and a
-- program-body registration would only take effect after the program had
-- been run at least once.

local completion = require "cc.shell.completion"

shell.setCompletionFunction("rom/programs/cassette.lua", completion.build(
    -- arg 1: subcommand name
    { completion.choice, { "play", "pause", "stop", "label", "volume", "write", "wget", "?" } },
    -- arg 2: depends on the subcommand.
    -- Note: `previous` contains the program name as its first entry
    -- (shell.complete passes the full tokenised line minus the current
    -- word), so the subcommand is `previous[2]`.
    function(shell, text, previous)
        local cmd = previous[2] and previous[2]:lower() or ""
        if cmd == "volume" then
            return completion.choice(shell, text, previous, { "0", "1", "2", "3" })
        elseif cmd == "write" then
            return completion.file(shell, text)
        end
        -- play/pause/stop/label/wget: no further arguments to complete
    end
))
