/**Study Helper
 * Hai-Au Bui
 * CSD230
 * Professor Ballou
 */
package edu.lwtech.studyhelper;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class StudyFetcher {
    //constants
    public static final String TYPE = "type";
    public static final String SUBJECTS = "subjects";
    public static final String SUBJECT = "subject";
    public static final String UPDATE_TIME = "updatetime";
    public static final String QUESTIONS = "questions";
    public static final String QUESTION = "question";
    public static final String ANSWER = "answer";

    public interface OnStudyDataReceivedListener {
        void onSubjectsReceived(List<Subject> subjects);
        void onQuestionsReceived(List<Question> questions);
        void onErrorResponse(VolleyError error);
    }

    //the link to get JSON file for subjects and questions
    private final String WEBAPI_BASE_URL = "https://wp.zybooks.com/study-helper.php";

    private RequestQueue mRequestQueue;


    public StudyFetcher(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public void fetchSubjects(final OnStudyDataReceivedListener listener) {

        String url = Uri.parse(WEBAPI_BASE_URL).buildUpon()
                .appendQueryParameter(TYPE, SUBJECTS).build().toString();

        // Request all subjects
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        List<Subject> subjects = parseJsonSubjects(response);
                        listener.onSubjectsReceived(subjects);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onErrorResponse(error);
                    }
                });

        mRequestQueue.add(request);
    }

    private List<Subject> parseJsonSubjects(JSONObject json) {

        List<Subject> subjects = new ArrayList<Subject>();

        // Create a list of subjects
        try {
            JSONArray subjectArray = json.getJSONArray(SUBJECTS);

            for (int i = 0; i < subjectArray.length(); i++) {
                JSONObject subjectObj = subjectArray.getJSONObject(i);

                Subject subject = new Subject();
                subject.setText(subjectObj.getString(SUBJECT));
                subject.setUpdateTime(subjectObj.getLong(UPDATE_TIME));
                subjects.add(subject);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return subjects;
    }

    public void fetchQuestions(Subject subject, final OnStudyDataReceivedListener listener) {

        String url = Uri.parse(WEBAPI_BASE_URL).buildUpon()
                .appendQueryParameter(TYPE, QUESTIONS)
                .appendQueryParameter(SUBJECT, subject.getText())
                .build().toString();

        // Request questions for this subject
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        List<Question> questions = parseJsonQuestions(response);
                        listener.onQuestionsReceived(questions);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onErrorResponse(error);
                    }
                });

        mRequestQueue.add(jsObjRequest);
    }

    private List<Question> parseJsonQuestions(JSONObject json) {

        List<Question> questions = new ArrayList<Question>();

        // Create a list of questions
        try {
            String subject = json.getString(SUBJECT);
            JSONArray questionArray = json.getJSONArray(QUESTIONS);

            for (int i = 0; i < questionArray.length(); i++) {
                JSONObject questionObj = questionArray.getJSONObject(i);

                Question question = new Question();
                question.setText(questionObj.getString(QUESTION));
                question.setAnswer(questionObj.getString(ANSWER));
                question.setSubject(subject);
                questions.add(question);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return questions;
    }
}