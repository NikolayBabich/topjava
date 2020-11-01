package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;

public interface CrudMealRepository extends JpaRepository<Meal, Integer> {
    @Transactional
    @Modifying
    @Query("DELETE FROM Meal m WHERE m.id = :id AND m.user.id = :userId")
    int delete(@Param("id") Integer id, @Param("userId") Integer userId);

    Meal findByIdAndUserId(Integer id, Integer userId);

    List<Meal> findAllByUserIdOrderByDateTimeDesc(Integer userId);

/*    List<Meal> findAllByUserIdAndDateTimeGreaterThanEqualAndDateTimeBeforeOrderByDateTimeDesc(Integer userId,
                                                                                                LocalDateTime startDateTime,
                                                                                                LocalDateTime endDateTime);
*/

    @Query("""
              SELECT m 
                FROM Meal m 
               WHERE m.user.id=:userId 
                     AND (m.dateTime >= :startDateTime AND m.dateTime < :endDateTime) 
            ORDER BY m.dateTime DESC
            """)
    List<Meal> findAllBetweenInclusive(@Param("userId") Integer userId,
                                       @Param("startDateTime") LocalDateTime startDateTime,
                                       @Param("endDateTime") LocalDateTime endDateTime);
}
