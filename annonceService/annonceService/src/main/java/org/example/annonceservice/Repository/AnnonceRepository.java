package org.example.annonceservice.Repository;
import org.example.annonceservice.Entity.Annonce;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnnonceRepository extends JpaRepository<Annonce, Integer> {
    List<Annonce> findByBloodTypeIgnoreCase(String bloodType);
    List<Annonce> findByStatusIgnoreCase(String status);
    List<Annonce> findByLocationId(Long locationId);
    List<Annonce> findByHospitalId(Long hospitalId);

}
