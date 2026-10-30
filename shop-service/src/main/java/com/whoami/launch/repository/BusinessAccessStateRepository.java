package com.whoami.launch.repository;
import com.whoami.launch.entity.BusinessAccessState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface BusinessAccessStateRepository
        extends JpaRepository<BusinessAccessState, Long> {
    Optional<BusinessAccessState> findByBusinessId(String businessId);
}
