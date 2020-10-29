package mcscheduler.logic.commands;

import static mcscheduler.logic.commands.CommandTestUtil.assertCommandSuccess;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import mcscheduler.commons.core.Messages;
import mcscheduler.commons.core.index.Index;
import mcscheduler.logic.commands.WorkerEditCommand.EditWorkerDescriptor;
import mcscheduler.model.McScheduler;
import mcscheduler.model.Model;
import mcscheduler.model.ModelManager;
import mcscheduler.model.UserPrefs;
import mcscheduler.model.worker.Worker;
import mcscheduler.testutil.EditWorkerDescriptorBuilder;
import mcscheduler.testutil.McSchedulerBuilder;
import mcscheduler.testutil.TypicalIndexes;
import mcscheduler.testutil.WorkerBuilder;

/**
 * Contains integration tests (interaction with the Model, UndoCommand and RedoCommand)
 * and unit tests for WorkerEditCommand.
 */
public class WorkerEditCommandTest {

    private Model model = new ModelManager(McSchedulerBuilder.getTypicalMcScheduler(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Worker editedWorker = new WorkerBuilder().build();
        EditWorkerDescriptor descriptor = new EditWorkerDescriptorBuilder(editedWorker).build();
        WorkerEditCommand workerEditCommand = new WorkerEditCommand(TypicalIndexes.INDEX_FIRST_WORKER, descriptor);

        String expectedMessage = String.format(WorkerEditCommand.MESSAGE_EDIT_WORKER_SUCCESS, editedWorker);

        Model expectedModel = new ModelManager(new McScheduler(model.getMcScheduler()), new UserPrefs());
        expectedModel.setWorker(model.getFilteredWorkerList().get(0), editedWorker);

        assertCommandSuccess(workerEditCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastWorker = Index.fromOneBased(model.getFilteredWorkerList().size());
        Worker lastWorker = model.getFilteredWorkerList().get(indexLastWorker.getZeroBased());

        WorkerBuilder workerInList = new WorkerBuilder(lastWorker);
        Worker editedWorker = workerInList.withName(CommandTestUtil.VALID_NAME_BOB).withPhone(
            CommandTestUtil.VALID_PHONE_BOB)
            .withRoles(CommandTestUtil.VALID_ROLE_CASHIER).build();

        EditWorkerDescriptor descriptor = new EditWorkerDescriptorBuilder().withName(CommandTestUtil.VALID_NAME_BOB)
            .withPhone(CommandTestUtil.VALID_PHONE_BOB).withRoles(CommandTestUtil.VALID_ROLE_CASHIER).build();
        WorkerEditCommand workerEditCommand = new WorkerEditCommand(indexLastWorker, descriptor);

        String expectedMessage = String.format(WorkerEditCommand.MESSAGE_EDIT_WORKER_SUCCESS, editedWorker);

        Model expectedModel = new ModelManager(new McScheduler(model.getMcScheduler()), new UserPrefs());
        expectedModel.setWorker(lastWorker, editedWorker);

        assertCommandSuccess(workerEditCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        WorkerEditCommand workerEditCommand =
            new WorkerEditCommand(TypicalIndexes.INDEX_FIRST_WORKER, new EditWorkerDescriptor());
        Worker editedWorker = model.getFilteredWorkerList().get(TypicalIndexes.INDEX_FIRST_WORKER.getZeroBased());

        String expectedMessage = String.format(WorkerEditCommand.MESSAGE_EDIT_WORKER_SUCCESS, editedWorker);

        Model expectedModel = new ModelManager(new McScheduler(model.getMcScheduler()), new UserPrefs());

        assertCommandSuccess(workerEditCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        CommandTestUtil.showWorkerAtIndex(model, TypicalIndexes.INDEX_FIRST_WORKER);

        Worker workerInFilteredList =
            model.getFilteredWorkerList().get(TypicalIndexes.INDEX_FIRST_WORKER.getZeroBased());
        Worker editedWorker = new WorkerBuilder(workerInFilteredList).withName(CommandTestUtil.VALID_NAME_BOB).build();
        WorkerEditCommand workerEditCommand = new WorkerEditCommand(TypicalIndexes.INDEX_FIRST_WORKER,
            new EditWorkerDescriptorBuilder().withName(CommandTestUtil.VALID_NAME_BOB).build());

        String expectedMessage = String.format(WorkerEditCommand.MESSAGE_EDIT_WORKER_SUCCESS, editedWorker);

        Model expectedModel = new ModelManager(new McScheduler(model.getMcScheduler()), new UserPrefs());
        expectedModel.setWorker(model.getFilteredWorkerList().get(0), editedWorker);

        assertCommandSuccess(workerEditCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateWorkerUnfilteredList_failure() {
        Worker firstWorker = model.getFilteredWorkerList().get(TypicalIndexes.INDEX_FIRST_WORKER.getZeroBased());
        EditWorkerDescriptor descriptor = new EditWorkerDescriptorBuilder(firstWorker).build();
        WorkerEditCommand workerEditCommand = new WorkerEditCommand(TypicalIndexes.INDEX_SECOND_WORKER, descriptor);

        CommandTestUtil.assertCommandFailure(workerEditCommand, model, WorkerEditCommand.MESSAGE_DUPLICATE_WORKER);
    }

    @Test
    public void execute_duplicateWorkerFilteredList_failure() {
        CommandTestUtil.showWorkerAtIndex(model, TypicalIndexes.INDEX_FIRST_WORKER);

        // edit worker in filtered list into a duplicate in address book
        Worker workerInList =
            model.getMcScheduler().getWorkerList().get(TypicalIndexes.INDEX_SECOND_WORKER.getZeroBased());
        WorkerEditCommand workerEditCommand = new WorkerEditCommand(TypicalIndexes.INDEX_FIRST_WORKER,
            new EditWorkerDescriptorBuilder(workerInList).build());

        CommandTestUtil.assertCommandFailure(workerEditCommand, model, WorkerEditCommand.MESSAGE_DUPLICATE_WORKER);
    }

    @Test
    public void execute_invalidWorkerIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredWorkerList().size() + 1);
        EditWorkerDescriptor descriptor =
            new EditWorkerDescriptorBuilder().withName(CommandTestUtil.VALID_NAME_BOB).build();
        WorkerEditCommand workerEditCommand = new WorkerEditCommand(outOfBoundIndex, descriptor);

        CommandTestUtil.assertCommandFailure(workerEditCommand, model, Messages.MESSAGE_INVALID_WORKER_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of address book
     */
    @Test
    public void execute_invalidWorkerIndexFilteredList_failure() {
        CommandTestUtil.showWorkerAtIndex(model, TypicalIndexes.INDEX_FIRST_WORKER);
        Index outOfBoundIndex = TypicalIndexes.INDEX_SECOND_WORKER;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getMcScheduler().getWorkerList().size());

        WorkerEditCommand workerEditCommand = new WorkerEditCommand(outOfBoundIndex,
            new EditWorkerDescriptorBuilder().withName(CommandTestUtil.VALID_NAME_BOB).build());

        CommandTestUtil.assertCommandFailure(workerEditCommand, model, Messages.MESSAGE_INVALID_WORKER_DISPLAYED_INDEX);
    }

    @Test
    public void execute_roleNotFound_throwsCommandException() {
        WorkerEditCommand editCommand = new WorkerEditCommand(TypicalIndexes.INDEX_FIRST_WORKER,
            new EditWorkerDescriptorBuilder().withRoles("random role").build());

        CommandTestUtil
            .assertCommandFailure(editCommand, model, String.format(Messages.MESSAGE_ROLE_NOT_FOUND, "Random role"));
    }

    @Test
    public void equals() {
        final WorkerEditCommand standardCommand =
            new WorkerEditCommand(TypicalIndexes.INDEX_FIRST_WORKER, CommandTestUtil.DESC_AMY);

        // same values -> returns true
        EditWorkerDescriptor copyDescriptor = new EditWorkerDescriptor(CommandTestUtil.DESC_AMY);
        WorkerEditCommand commandWithSameValues =
            new WorkerEditCommand(TypicalIndexes.INDEX_FIRST_WORKER, copyDescriptor);
        assertEquals(commandWithSameValues, standardCommand);

        // same object -> returns true
        assertEquals(standardCommand, standardCommand);

        // null -> returns false
        assertNotEquals(standardCommand, null);

        // different types -> returns false
        assertNotEquals(new ClearCommand(), standardCommand);

        // different index -> returns false
        assertNotEquals(new WorkerEditCommand(TypicalIndexes.INDEX_SECOND_WORKER, CommandTestUtil.DESC_AMY),
            standardCommand);

        // different descriptor -> returns false
        assertNotEquals(new WorkerEditCommand(TypicalIndexes.INDEX_FIRST_WORKER, CommandTestUtil.DESC_BOB),
            standardCommand);
    }

}