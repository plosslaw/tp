package seedu.address.model.assignment;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Role;

/**
 * Represents a Worker being assigned to a Role for a shift in the App.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class WorkerRolePair {

    public static final String MESSAGE_CONSTRAINTS = "Worker-Role Pair must be of the form [ WORKER_INDEX ] [ ROLE ]"
            + "(e.g. \'2 Cashier\').";

    public static final String VALIDATION_REGEX = "[1-9][0-9]* " + Role.VALIDATION_REGEX;

    private final Index workerIndex;
    private final Role role;

    /**
     * Every field must be present and not null.
     */
    public WorkerRolePair(Index workerIndex, Role role) {
        requireAllNonNull(workerIndex, role);
        this.workerIndex = workerIndex;
        this.role = role;
    }

    /**
     * String version constructor for easy parsing of sample data.
     */
    public WorkerRolePair(String workerRoleInfo) throws ParseException {
        requireNonNull(workerRoleInfo);
        checkArgument(isValidWorkerRolePair(workerRoleInfo), MESSAGE_CONSTRAINTS);
        int index = workerRoleInfo.indexOf(" ");
        this.workerIndex = ParserUtil.parseIndex(workerRoleInfo.substring(0, index));
        this.role = Role.createRole(workerRoleInfo.substring(index + 1));
    }

    /**
     * Returns true if a given string is a valid worker-role
     */
    public static boolean isValidWorkerRolePair(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    public Role getRole() {
        return role;
    }

    public Index getWorkerIndex() {
        return workerIndex;
    }

    /**
     * Returns true only if both worker-role are about the same worker and the same role.
     */
    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof WorkerRolePair
                && workerIndex.equals(((WorkerRolePair) other).workerIndex)
                && role.equals(((WorkerRolePair) other).role));
    }

    @Override
    public int hashCode() {
        return Objects.hash(workerIndex, role);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(" Assigning Worker index ")
                .append(getWorkerIndex())
                .append(" to Role: ")
                .append(getRole());
        return builder.toString();
    }

}