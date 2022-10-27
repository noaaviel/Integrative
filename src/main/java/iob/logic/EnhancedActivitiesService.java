package iob.logic;

import java.util.Date;
import java.util.List;

import iob.Boundaries.ActivityBoundary;

public interface EnhancedActivitiesService extends ActivitiesService {
	public List<ActivityBoundary> getAllActivities(int size, int page);

	public List<ActivityBoundary> getAllActivitiesByType(String type, int size, int page);

	public List<ActivityBoundary> getAllActivitiesByCreatedTimestampBefore(Date date, int size, int page);

	public List<ActivityBoundary> getAllIActivitiesByCreatedTimestampAfter(Date date, int size, int page);
}