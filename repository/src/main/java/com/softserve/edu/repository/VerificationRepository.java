package com.softserve.edu.repository;

import com.softserve.edu.entity.Verification;
/*import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;*/
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface VerificationRepository extends CrudRepository<Verification, String> {
      Page<Verification> findAll(Pageable pageable);

    /*//@Query("select o from Organization o where upper(o.name) like %upper(:search)%")
    Page<Verification> findByNameLikeIgnoreCase(String name, Pageable pageable);*/
}
