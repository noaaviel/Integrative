package iob.DAOs;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.repository.query.Param;

import iob.basics.UserId;
import iob.data.UserEntity;
import iob.data.UserRole;
@EnableMongoRepositories
public interface UsersDAO extends MongoRepository<UserEntity, UserId> {
	// Methods
	public List<UserEntity> getAllUsersByRole(@Param("role") UserRole role, Pageable page);

	public List<UserEntity> getAllUsersByUsername(@Param("username") String username, Pageable page);

	public List<UserEntity> getAllUsersByAvatar(@Param("avatar") String avatar, Pageable page);

}