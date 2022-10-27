package iob.logic;

import java.util.List;

import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;

import iob.Boundaries.InstanceBoundary;
import iob.basics.CreatedBy;
import iob.basics.UserId;

public interface EnhancedInstancesService extends InstancesService {
	public List<InstanceBoundary> getAllInstances(int size, int page);

	public List<InstanceBoundary> getAllInstancesByName(String name, int size, int page);

	public List<InstanceBoundary> getAllInstancesByType(String type, int size, int page);
	
	public List<InstanceBoundary> getAllInstancesNear(Point location, Distance distance);

}