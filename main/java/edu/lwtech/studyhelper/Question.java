/**Study Helper
 * Hai-Au Bui
 * CSD230
 * Professor Ballou
 */
package edu.lwtech.studyhelper;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "questions",
        foreignKeys = @ForeignKey(onDelete = ForeignKey.CASCADE,
                entity = Subject.class,
                parentColumns = "text",
                childColumns = "subject"),
                indices = {@Index("subject")})
public class Question {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    @ColumnInfo(name = "text")
    private String mText;

    @ColumnInfo(name = "answer")
    private String mAnswer;


    @ColumnInfo(name = "subject")
    private String mSubject;

    public String getText() {
        return mText;
    }

    public void setId(long id) {
        mId = id;
    }

    public long getId() {
        return mId;
    }

    public void setText(String text) {
        this.mText = text;
    }

    public String getAnswer() {
        return mAnswer;
    }

    public void setAnswer(String answer) {
        this.mAnswer = answer;
    }

    public String getSubject() {
        return mSubject;
    }

    public void setSubject(String subject) {
        this.mSubject = subject;
    }
}
