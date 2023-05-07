package zerobase.dividend.persist;

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zerobase.dividend.persist.entity.DividendEntity;

@Repository
public interface DividendRepository extends JpaRepository<DividendEntity, Long> {
	List<DividendEntity> findAllByCompanyId(Long id);
	
	@Transactional
    void deleteAllByCompanyId(Long id);
	
	boolean existsByCompanyIdAndDate(Long companyId, LocalDateTime date);
}
