/**Study Helper
 * Hai-Au Bui
 * CSD230
 * Professor Ballou
 */
package edu.lwtech.studyhelper;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface QuestionDao {

    @Query("SELECT * FROM questions WHERE id = :id")
    public Question getQuestion(long id);

    @Query("SELECT * FROM questions WHERE subject = :subject")
    public List<Question> getQuestions(String subject);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insertQuestion(Question question);

    @Update
    public void updateQuestion(Question question);

    @Delete
    public void deleteQuestion(Question question);
}