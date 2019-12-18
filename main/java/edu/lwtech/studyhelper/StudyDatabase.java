/**Study Helper
 * Hai-Au Bui
 * CSD230
 * Professor Ballou
 */

package edu.lwtech.studyhelper;
import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Question.class, Subject.class}, version = 1, exportSchema = false)
public abstract class StudyDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "study2.db";

    private static StudyDatabase mStudyDatabase;

    // Singleton
    public static StudyDatabase getInstance(Context context) {
        if (mStudyDatabase == null) {
            mStudyDatabase = Room.databaseBuilder(context, StudyDatabase.class,
                    DATABASE_NAME).allowMainThreadQueries().build();
        }
        return mStudyDatabase;
    }

    public abstract QuestionDao questionDao();
    public abstract SubjectDao subjectDao();
}

