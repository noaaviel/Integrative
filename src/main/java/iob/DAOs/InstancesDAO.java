package iob.DAOs;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Streamable;

import iob.Boundaries.InstanceBoundary;
import iob.basics.InstanceId;
import iob.data.InstanceEntity;
@EnableMongoRepositories
public interface InstancesDAO extends MongoRepository<InstanceEntity, InstanceId> {
	// Methods
	public List<InstanceEntity> getAllInstancesByName(@Param("name") String name, Pageable page);

	public List<InstanceEntity> getAllInstancesByType(@Param("type") String type, Pageable page);
	
	public List<InstanceEntity> getAllByCreatedByDomainAndCreatedByEmail(@Param("createdByDomain") String domain, @Param("createdByEmail") String email, Pageable page);
	
	public List<InstanceEntity> getAllByLocationNear(@Param("location") Point point, @Param("maxDistance") Distance distance);

	public List<InstanceEntity> getAllByInstanceAttributesContainingAndLocationNear(@Param("instanceAttributes")String str, @Param("location")Point point, @Param("maxDistance")Distance radius);

	public List<InstanceEntity> getAllByInstanceAttributesContainingAndLocationNearAndNameContaining(
			@Param("instanceAttributes")String str, @Param("location")Point point, @Param("maxDistance")Distance radius, @Param("name")String name);
}