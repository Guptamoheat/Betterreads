package com.mohit.betterreads.userBooks;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.CassandraType.Name;

@PrimaryKeyClass
public class UserBookPrimaryKey {
    @PrimaryKeyColumn(type=PrimaryKeyType.PARTITIONED, name = "user_id", ordinal = 0)
    @CassandraType(type = Name.TEXT)
    private String userId;

    @PrimaryKeyColumn(type=PrimaryKeyType.PARTITIONED, name = "book_id", ordinal = 1)
    @CassandraType(type = Name.TEXT)
    private String bookId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    


}
