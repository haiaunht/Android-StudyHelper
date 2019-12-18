/**Study Helper
 * Hai-Au Bui
 * CSD230
 * Professor Ballou
 */
package edu.lwtech.studyhelper;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.VolleyError;
import java.util.List;

public class ImportActivity extends AppCompatActivity {
    //constants
    public static final String IMPORTED_SUCCESSFULLY = " imported successfully";
    public static final String ERROR_LOADING_SUBJECTS_TRY_AGAIN_LATER = "Error loading subjects. Try again later.";
    public static final String IS_ALREADY_IMPORTED = " is already imported.";

    //data members
    private LinearLayout mSubjectLayoutContainer;
    private StudyFetcher mStudyFetcher;
    private ProgressBar mLoadingProgressBar;

    /**
     * onCreate() will set progressbar to visible, create new instance of StudyFetcher object
     * and call its fetchSubjects() method and passing onStudyDataReceivedListener obj
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        mSubjectLayoutContainer = findViewById(R.id.subjectLayout);

        // Show progress bar
        mLoadingProgressBar = findViewById(R.id.loadingProgressBar);
        mLoadingProgressBar.setVisibility(View.VISIBLE);

        mStudyFetcher = new StudyFetcher(this);
        mStudyFetcher.fetchSubjects(mFetchListener);
    }

    private StudyFetcher.OnStudyDataReceivedListener mFetchListener = new StudyFetcher.OnStudyDataReceivedListener() {

        @Override
        public void onSubjectsReceived(List<Subject> subjects) {

            // Hide progress bar
            mLoadingProgressBar.setVisibility(View.GONE);

            // Create a checkbox for each subject
            for (Subject subject: subjects) {
                CheckBox checkBox = new CheckBox(getApplicationContext());
                checkBox.setTextSize(24);
                checkBox.setText(subject.getText());
                checkBox.setTag(subject);
                mSubjectLayoutContainer.addView(checkBox);
            }
        }

        @Override
        public void onQuestionsReceived(List<Question> questions) {

            if (questions.size() > 0) {
                StudyDatabase studyDb = StudyDatabase.getInstance(getApplicationContext());

                // Add the questions to the database
                for (Question question : questions) {
                    studyDb.questionDao().insertQuestion(question);
                }

                String subject = questions.get(0).getSubject();
                Toast.makeText(getApplicationContext(), subject + IMPORTED_SUCCESSFULLY,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), ERROR_LOADING_SUBJECTS_TRY_AGAIN_LATER,
                    Toast.LENGTH_LONG).show();
            mLoadingProgressBar.setVisibility(View.GONE);
        }
    };

    //add subject to database, if subject is already exist, will display an error message
    public void importButtonClick(View view) {

        StudyDatabase dbHelper = StudyDatabase.getInstance(getApplicationContext());

        // Determine which subjects were selected
        int numCheckBoxes = mSubjectLayoutContainer.getChildCount();
        for (int i = 0; i < numCheckBoxes; i++) {
            CheckBox checkBox = (CheckBox) mSubjectLayoutContainer.getChildAt(i);
            if (checkBox.isChecked()) {
                Subject subject = (Subject) checkBox.getTag();

                //trying adding new subject, if already exist display the Toast for user to see
                try {
                    // Add subject to the database
                    dbHelper.subjectDao().insertSubject(subject);
                    mStudyFetcher.fetchQuestions(subject, mFetchListener);
                }catch (Exception e){
                    Toast.makeText(this, subject.getText() + IS_ALREADY_IMPORTED, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}