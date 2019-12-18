/**Study Helper
 * Hai-Au Bui
 * CSD230
 * Professor Ballou
 */
package edu.lwtech.studyhelper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class QuestionActivity extends AppCompatActivity {
    //constant
    public static final String EXTRA_SUBJECT = "edu.lwtech.studyhelper.subject";
    private final int REQUEST_CODE_NEW_QUESTION = 0;
    private final int REQUEST_CODE_UPDATE_QUESTION = 1;

    //data members
    private StudyDatabase mStudyDb;
    private String mSubject;
    private List<Question> mQuestionList;
    private TextView mAnswerLabel;
    private TextView mAnswerText;
    private Button mAnswerButton;
    private TextView mQuestionText;
    private int mCurrentQuestionIndex;
    private ViewGroup mShowQuestionsLayout;
    private ViewGroup mNoQuestionsLayout;
    private Question mDeletedQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        // Hosting activity provides the subject of the questions to display
        Intent intent = getIntent();
        mSubject = intent.getStringExtra(EXTRA_SUBJECT);

        // Load all questions for this subject
        mStudyDb = StudyDatabase.getInstance(getApplicationContext());


        mQuestionList = mStudyDb.questionDao().getQuestions(mSubject);

        mQuestionText = findViewById(R.id.questionText);
        mAnswerLabel = findViewById(R.id.answerLabel);
        mAnswerText = findViewById(R.id.answerText);
        mAnswerButton = findViewById(R.id.answerButton);
        mShowQuestionsLayout = findViewById(R.id.showQuestionsLayout);
        mNoQuestionsLayout = findViewById(R.id.noQuestionsLayout);

        // Show first question
        showQuestion(0);
    }

    //display questions if there is any
    @Override
    protected void onStart() {
        super.onStart();

        // Are there questions to display?
        if (mQuestionList.size() == 0) {
            updateAppBarTitle();
            displayQuestion(false);
        } else {
            displayQuestion(true);
            toggleAnswerVisibility();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate menu for the app bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.question_menu, menu);
        return true;
    }

    //menu for previous, next, overflow: add, edit, delete
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Determine which app bar item was chosen
        switch (item.getItemId()) {
            case R.id.previous:
                showQuestion(mCurrentQuestionIndex - 1);
                return true;
            case R.id.next:
                showQuestion(mCurrentQuestionIndex + 1);
                return true;
            case R.id.add:
                addQuestion();
                return true;
            case R.id.edit:
                editQuestion();
                return true;
            case R.id.delete:
                deleteQuestion();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addQuestionButtonClick(View view) {
        addQuestion();
    }

    public void answerButtonClick(View view) {
        toggleAnswerVisibility();
    }

    private void displayQuestion(boolean display) {

        // Show or hide the appropriate screen
        if (display) {
            mShowQuestionsLayout.setVisibility(View.VISIBLE);
            mNoQuestionsLayout.setVisibility(View.GONE);
        } else {
            mShowQuestionsLayout.setVisibility(View.GONE);
            mNoQuestionsLayout.setVisibility(View.VISIBLE);
        }
    }

    //update the app bar title
    private void updateAppBarTitle() {

        // Display subject and number of questions in app bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            String title = getResources().getString(R.string.question_number,
                    mSubject, mCurrentQuestionIndex + 1, mQuestionList.size());
            setTitle(title);
        }
    }

    //adding question
    private void addQuestion() {
        Intent intent = new Intent(this, QuestionEditActivity.class);
        intent.putExtra(QuestionEditActivity.EXTRA_SUBJECT, mSubject);
        startActivityForResult(intent, REQUEST_CODE_NEW_QUESTION);
    }

    //edit an existing question
    private void editQuestion() {
        if (mCurrentQuestionIndex >= 0) {
            Intent intent = new Intent(this, QuestionEditActivity.class);
            intent.putExtra(EXTRA_SUBJECT, mSubject);
            long questionId = mQuestionList.get(mCurrentQuestionIndex).getId();
            intent.putExtra(QuestionEditActivity.EXTRA_QUESTION_ID, questionId);
            startActivityForResult(intent, REQUEST_CODE_UPDATE_QUESTION);
        }
    }

    //adding new question or update the edited question via request code
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //request code = 0, adding new question
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_NEW_QUESTION) {
            long questionId = data.getLongExtra(QuestionEditActivity.EXTRA_QUESTION_ID, -1);

            Question newQuestion = mStudyDb.questionDao().getQuestion(questionId);
            mQuestionList.add(newQuestion);
            showQuestion(mQuestionList.size() - 1);
        }
        //request code = 1, update question
        else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_UPDATE_QUESTION) {
            // Get updated question
            long questionId = data.getLongExtra(QuestionEditActivity.EXTRA_QUESTION_ID, -1);

            Question updatedQuestion = mStudyDb.questionDao().getQuestion(questionId);

            // Replace current question in question list with updated question
            Question currentQuestion = mQuestionList.get(mCurrentQuestionIndex);
            currentQuestion.setText(updatedQuestion.getText());
            currentQuestion.setAnswer(updatedQuestion.getAnswer());
            showQuestion(mCurrentQuestionIndex);

            Toast.makeText(this, R.string.question_updated, Toast.LENGTH_SHORT).show();
        }
    }

    //delete the chosen question
    private void deleteQuestion() {
        if (mCurrentQuestionIndex >= 0) {
            // Save question in case user undoes delete
            mDeletedQuestion = mQuestionList.get(mCurrentQuestionIndex);

            //delete question
            mStudyDb.questionDao().deleteQuestion(mDeletedQuestion);
            mQuestionList.remove(mCurrentQuestionIndex);

            if (mQuestionList.size() == 0) {
                // No questions to show
                mCurrentQuestionIndex = -1;
                updateAppBarTitle();
                displayQuestion(false);
            } else {
                showQuestion(mCurrentQuestionIndex);
            }

            // Show delete message with Undo button
            Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout),
                    R.string.question_deleted, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Add question back
                    mStudyDb.questionDao().insertQuestion(mDeletedQuestion);
                    mQuestionList.add(mDeletedQuestion);
                    showQuestion(mQuestionList.size() - 1);
                    displayQuestion(true);
                }
            });
            snackbar.show();
        }
    }

    //display the question
    private void showQuestion(int questionIndex) {

        // Show question at the given index
        if (mQuestionList.size() > 0) {
            if (questionIndex < 0) {
                questionIndex = mQuestionList.size() - 1;
            } else if (questionIndex >= mQuestionList.size()) {
                questionIndex = 0;
            }

            mCurrentQuestionIndex = questionIndex;
            updateAppBarTitle();

            Question question = mQuestionList.get(mCurrentQuestionIndex);
            mQuestionText.setText(question.getText());
            mAnswerText.setText(question.getAnswer());
        }
        else {
            // No questions yet
            mCurrentQuestionIndex = -1;
        }
    }

    private void toggleAnswerVisibility() {
        if (mAnswerText.getVisibility() == View.VISIBLE) {
            mAnswerButton.setText(R.string.show_answer);
            mAnswerText.setVisibility(View.INVISIBLE);
            mAnswerLabel.setVisibility(View.INVISIBLE);
        }
        else {
            mAnswerButton.setText(R.string.hide_answer);
            mAnswerText.setVisibility(View.VISIBLE);
            mAnswerLabel.setVisibility(View.VISIBLE);
        }
    }
}