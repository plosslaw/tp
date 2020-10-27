package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROLE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SHIFT_NEW;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SHIFT_OLD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WORKER_NEW;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WORKER_OLD;

import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.ReassignCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Role;


public class ReassignCommandParser implements Parser<ReassignCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the ReassignCommand
     * and returns an ReassignCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public ReassignCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_WORKER_OLD, PREFIX_WORKER_NEW, PREFIX_SHIFT_OLD, PREFIX_SHIFT_NEW,
                PREFIX_ROLE);

        if (!arePrefixesPresent(argMultimap, PREFIX_WORKER_OLD, PREFIX_WORKER_NEW, PREFIX_SHIFT_OLD,
                PREFIX_SHIFT_NEW, PREFIX_ROLE)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ReassignCommand.MESSAGE_USAGE));
        }

        Index oldShiftIndex;
        Index newShiftIndex;
        Index oldWorkerIndex;
        Index newWorkerIndex;
        try {
            oldShiftIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_SHIFT_OLD).get());
            newShiftIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_SHIFT_NEW).get());
            oldWorkerIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_WORKER_OLD).get());
            newWorkerIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_WORKER_NEW).get());
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ReassignCommand.MESSAGE_USAGE), ive);
        }

        Role newRole = ParserUtil.parseRole(argMultimap.getValue(PREFIX_ROLE).get());

        return new ReassignCommand(oldWorkerIndex, newWorkerIndex, oldShiftIndex, newShiftIndex, newRole);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
