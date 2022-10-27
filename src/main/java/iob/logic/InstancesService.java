package iob.logic;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import iob.Boundaries.InstanceBoundary;

public interface InstancesService {
	public InstanceBoundary createInstance(InstanceBoundary instance, String userEmail);

	public InstanceBoundary updateInstance(String instanceDomain, String instanceId, InstanceBoundary update)
			throws JsonMappingException, JsonProcessingException;

	public InstanceBoundary getSpecificInstance(String instanceDomain, String instanceId);

	public List<InstanceBoundary> getAllInstances();

	public void deleteAllInstances();
}