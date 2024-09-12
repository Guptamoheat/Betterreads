package com.mohit.betterreads.userBooks;

import org.springframework.data.cassandra.repository.CassandraRepository;


public interface UserBooksRepository extends CassandraRepository<UserBooks, UserBookPrimaryKey>{
    
    
}
