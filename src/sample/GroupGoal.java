package sample;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.util.Set;

/**
 * Class for modelling a group user goal in the ProActive app by its target amount, concerned units, end date,
 * current progress, and completion status.
 *
 * @author Evan Clayton
 *
 * @version 1
 *
 * @see Goal
 *
 * 1.0 - First working version.
 */
public class GroupGoal extends IndividualGoal {
    private Group group;
    private boolean accepted;
    /**
     * If the goal was derived from a group goal it will have a group id, else this value will be 0
     */
    protected int group_id;

    /**
     * Constructs a goal from a target amount, unit, and end date. Initialises progress to 0 and status to ongoing.
     * Intended for use at initial creation of a goal.
     *
     * @param target  the target amount of the goal.
     * @param unit    the units targeted by the goal.
     * @param endDate the end date of the goal.
     * @param group_id the id of the group associated with the goal.
     */
    public GroupGoal(float target, Unit unit, LocalDate endDate, int group_id) {
        super(target, unit, endDate);
        this.group_id = group_id;
        DatabaseHandler db = DatabaseHandler.getInstance();
        this.group = db.getGroupObjectFromGroupId(group_id);
    }

    /**
     * Constructs a goal from a target amount, unit, end date, progress amount, and current status. Intended for use
     * when pulling group goals from the database.
     *
     * @param target   the target amount of the goal.
     * @param unit     the units targeted by the goal.
     * @param endDate  the end date of the goal.
     * @param progress the current progress of the goal.
     * @param group_id the id of the group associated with the goal.
     */
    public GroupGoal(float target, Unit unit, LocalDate endDate, int progress, int group_id, boolean accepted) {
        super(target, unit, endDate, progress);
        this.group_id = group_id;
        this.accepted = accepted;
        DatabaseHandler db = DatabaseHandler.getInstance();
        this.group = db.getGroupObjectFromGroupId(group_id);
    }

    public GroupGoal(float target, Unit unit, LocalDate endDate, int progress, int group_id) {
        super(target, unit, endDate, progress);

        this.group_id = group_id;
    }

    public Group getGroup() { return group; }

    public void setGroup(Group group) { this.group = group; }

    public boolean isAccepted() { return accepted; }

    public void setAccepted(boolean accepted) { this.accepted = accepted; }

    /**
     * Gets the group_id for the goal (Note this is default 0 if no group is associated with this goal).
     *
     * @return the group_id for this goal.
     */
    public int getGroup_id() { return group_id; }

}

