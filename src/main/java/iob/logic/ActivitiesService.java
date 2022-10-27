package iob.logic;

import java.util.List;

import iob.Boundaries.ActivityBoundary;

public interface ActivitiesService {
	public Object invokeActivity(ActivityBoundary activity, String userEmail);

	public List<ActivityBoundary> getAllActivities();

	public void deleteAllActivities();
}