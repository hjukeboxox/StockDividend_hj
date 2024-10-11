package com.dayone.persist;

import com.dayone.persist.entity.CompanyEntity;
import com.dayone.persist.entity.DividendEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    boolean existsByTicker(String ticker);
    Optional<CompanyEntity> findByName(String name);

    Optional<CompanyEntity> findByTicker(String ticker);

    //Like연산 사용하기
    Page<CompanyEntity> findByNameStartingWithIgnoreCase(String s, Pageable pageable);

}
