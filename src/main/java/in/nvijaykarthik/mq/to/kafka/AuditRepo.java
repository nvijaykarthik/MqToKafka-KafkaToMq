package in.nvijaykarthik.mq.to.kafka;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepo extends JpaRepository<AuditEntity, Long>{

}
