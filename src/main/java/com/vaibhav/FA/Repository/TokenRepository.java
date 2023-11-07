package com.vaibhav.FA.Repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.vaibhav.FA.model.VerificationToken;

@Repository
public interface TokenRepository extends CrudRepository<VerificationToken,String> {}
