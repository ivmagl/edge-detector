package xyz.imahlatii.edgedetector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.imahlatii.edgedetector.model.DBFile;

@Repository
public interface DBFileRepository extends JpaRepository<DBFile, String> {

}
