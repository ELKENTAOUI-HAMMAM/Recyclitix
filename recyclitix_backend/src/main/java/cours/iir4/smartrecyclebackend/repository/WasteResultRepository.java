package cours.iir4.smartrecyclebackend.repository;

import cours.iir4.smartrecyclebackend.model.WasteResult;
import cours.iir4.smartrecyclebackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface WasteResultRepository extends JpaRepository<WasteResult, Long> {
    
    
    List<WasteResult> findByUser(User user);
    
    
    List<WasteResult> findByUserOrderByScanTimeDesc(User user);
    
    
    List<WasteResult> findByWasteType(String wasteType);
}