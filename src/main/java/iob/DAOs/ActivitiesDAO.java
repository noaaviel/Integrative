package iob.DAOs;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.repository.query.Param;

import iob.data.ActivityEntity;
@EnableMongoRepositories
public interface ActivitiesDAO extends MongoRepository<ActivityEntity, String> {
	// Methods
	public List<ActivityEntity> getAllActivitiesByType(@Param("type") String type, Pageable page);

	public List<ActivityEntity> getAllActivitiesByCreatedTimestampBefore(@Param("date") Date date, Pageable page);

	public List<ActivityEntity> getAllIActivitiesByCreatedTimestampAfter(@Param("date") Date date, Pageable page);
}