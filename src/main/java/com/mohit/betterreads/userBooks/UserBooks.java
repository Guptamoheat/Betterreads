package com.mohit.betterreads.userBooks;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.CassandraType.Name;

@Table("book_by_userId_and_bookId")
public class UserBooks {
    
    @Id @PrimaryKey
    private UserBookPrimaryKey userBookPrimaryKey;

    @Column("start_date")
    @CassandraType(type = Name.DATE)
    private LocalDate startedDate;

    @Column("end_date")
    @CassandraType(type = Name.DATE)
    private LocalDate completedDate;

    @Column("rating")
    @CassandraType(type = Name.INT)
    private int rating;

    @Column("reading_status")
    @CassandraType(type = Name.TEXT)
    private String readingStatus;

    public UserBookPrimaryKey getUserBookPrimaryKey() {
        return userBookPrimaryKey;
    }

    public void setUserBookPrimaryKey(UserBookPrimaryKey userBookPrimaryKey) {
        this.userBookPrimaryKey = userBookPrimaryKey;
    }

    

    public LocalDate getStartedDate() {
        return startedDate;
    }

    public void setStartedDate(LocalDate startedDate) {
        this.startedDate = startedDate;
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDate completedDate) {
        this.completedDate = completedDate;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReadingStatus() {
        return readingStatus;
    }

    public void setReadingStatus(String readingStatus) {
        this.readingStatus = readingStatus;
    }

    @Override
    public String toString() {
        return "UserBooks [userBookPrimaryKey=" + userBookPrimaryKey + ", startedDate=" + startedDate
                + ", completedDate=" + completedDate + ", rating=" + rating + ", readingStatus=" + readingStatus + "]";
    }

    

    
}
