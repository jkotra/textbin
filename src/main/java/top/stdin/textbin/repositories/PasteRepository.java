package top.stdin.textbin.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import top.stdin.textbin.entities.Paste;

@Repository
public interface PasteRepository extends PagingAndSortingRepository<Paste, Double> {
	
	List<Paste> findByUuid(UUID uuid);
	List<Paste> findTop5ByOrderByIdDesc();
	
}
