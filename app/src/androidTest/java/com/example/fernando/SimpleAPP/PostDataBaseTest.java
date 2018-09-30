package com.example.fernando.SimpleAPP;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;

public class PostDataBaseTest {

    ArrayList<Post> postList = new ArrayList<>();
    private Context instrumentationCtx;
    private PostDataBase postDataBase;

    @Before
    public void setUp() {
        instrumentationCtx = InstrumentationRegistry.getTargetContext();
        postDataBase = new PostDataBase(instrumentationCtx);
        for (int i = 0; i < 10000; i++) {
            Post post = new Post(Calendar.getInstance().getTime().getTime(),
                    "Fernando",
                    "Test Post",
                    "https://www.google.cl",
                    "" + i, 0);
            postList.add(post);
        }
    }


    @Test
    public void storePosts() {

        postDataBase.storePosts(postList);
    }

    @Test
    public void getPosts() {
        postList = postDataBase.getPosts();
    }

    @Test
    public void deletePost() {

        postList = postDataBase.getPosts();
        if (postList.size() < 0) {
            for (int i = 0; i < postList.size(); i++) {
                postDataBase.deletePost(postList.get(i));
            }
        }
    }

    @Test
    public void ClearDataBase() {
        postDataBase.ClearDB();
    }
}