package cours.iir4.smartrecyclebackend.repository;

import cours.iir4.smartrecyclebackend.model.WasteResult;
import cours.iir4.smartrecyclebackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for WasteResult entity
 */
@Repository
public interface WasteResultRepository extends JpaRepository<WasteResult, Long> {
    
    /**
     * Find all waste results for a specific user
     * @param user the user whose waste results to find
     * @return a list of waste results for the user
     */
    List<WasteResult> findByUser(User user);
    
    /**
     * Find all waste results for a specific user, ordered by scan time (most recent first)
     * @param user the user whose waste results to find
     * @return a list of waste results for the user, ordered by scan time
     */
    List<WasteResult> findByUserOrderByScanTimeDesc(User user);
    
    /**
     * Find all waste results of a specific waste type
     * @param wasteType the waste type to search for
     * @return a list of waste results of the specified type
     */
    List<WasteResult> findByWasteType(String wasteType);
}