package example.server;


import java.net.URL;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.scribe.builder.*;
import org.scribe.builder.api.*;
import org.scribe.model.*;
import org.scribe.oauth.*;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;

import example.server.Post;


public class FacebookRestClientRMI {
	private static final String NETWORK_NAME = "Facebook";
	private static String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/me";
	private static final Token EMPTY_TOKEN = null;
	private static String apiKey = " 384192551669323";
	private static String apiSecret = "1e1f45853d06776f4de6c06954120dfc";
	private static OAuthService service = new ServiceBuilder()
			.provider(FacebookApi.class).apiKey(apiKey).apiSecret(apiSecret)
			.callback("http://eden.dei.uc.pt/~amaf/echo.php") // Do not change
																// this.
			.scope("publish_stream, read_stream").build();


	private ClientRMI app;

	
	public FacebookRestClientRMI(ClientRMI app)
	{
		this.app=app;
		
	}


	public void postsByFacebook() {

	    System.out.println("Enter the verifier:");
	    Scanner in = new Scanner(System.in);
	    Verifier verifier = new Verifier(in.nextLine());
		Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
		
		HashMap<String, String> postsF = new HashMap<String, String>();
		
		PROTECTED_RESOURCE_URL = "https://api.facebook.com/method/users.getLoggedInUser&format=json";
		OAuthRequest request = new OAuthRequest(Verb.GET,
		PROTECTED_RESOURCE_URL);
		service.signRequest(accessToken, request);
		Response response = request.send();
		String id = response.getBody();

		PROTECTED_RESOURCE_URL = "https://api.facebook.com/method/stream.get?viewer_id="
				+ id + "&format=json";
		request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
		service.signRequest(accessToken, request);
		response = request.send();

		JSONObject jposts = (JSONObject) JSONValue.parse(response.getBody());
		JSONArray posts = (JSONArray) jposts.get("posts");
		
		for(int j=0; j<posts.size(); j++)
		{
			JSONObject post = (JSONObject) posts.get(j);
			postsF.put( (String) post.get("post_id"), (String) post.get("message"));
		}
		
		app.addPostsF(postsF);

	}


	public int addComment(String comment, Post post) {
		
	    System.out.println("Enter the verifier:");
	    Scanner in = new Scanner(System.in);
	    Verifier verifier = new Verifier(in.nextLine());
		Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
		

		String postID = post.getFacebook();
		System.out.println(postID);

		comment = comment.replace(" ","+");
		
		PROTECTED_RESOURCE_URL = "https://api.facebook.com/method/users.getLoggedInUser&format=json";
		OAuthRequest request = new OAuthRequest(Verb.GET,
				PROTECTED_RESOURCE_URL);
		service.signRequest(accessToken, request);
		Response response = request.send();
		String id = response.getBody();

		PROTECTED_RESOURCE_URL = "https://api.facebook.com/method/stream.addComment?uid="
				+ id + "&format=json&post_id=" + postID + "&comment=" + comment;
		request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
		service.signRequest(accessToken, request);
		response = request.send();



		return response.getCode();

	}

	public int deletePost(Post post) {
		
	    System.out.println("Enter the verifier:");
	    Scanner in = new Scanner(System.in);
	    Verifier verifier = new Verifier(in.nextLine());
		Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
		
		String postID = post.getFacebook();

		PROTECTED_RESOURCE_URL = "https://api.facebook.com/method/users.getLoggedInUser&format=json";
		OAuthRequest request = new OAuthRequest(Verb.GET,
				PROTECTED_RESOURCE_URL);
		service.signRequest(accessToken, request);
		Response response = request.send();
		String id = response.getBody();

		PROTECTED_RESOURCE_URL = "https://api.facebook.com/method/stream.remove?uid="
				+ id + "&format=json&post_id=" + postID;
		request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
		service.signRequest(accessToken, request);
		response = request.send();

		return response.getCode();
	}

	public String post(String post) {
		
	    System.out.println("Enter the verifier:");
	    Scanner in = new Scanner(System.in);
	    Verifier verifier = new Verifier(in.nextLine());
		Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
		
		String postF =post.replace(" ","+");


		PROTECTED_RESOURCE_URL = "https://api.facebook.com/method/users.getLoggedInUser&format=json";
		OAuthRequest request = new OAuthRequest(Verb.GET,PROTECTED_RESOURCE_URL);
		service.signRequest(accessToken, request);
		Response response = request.send();
		String id = response.getBody();

		PROTECTED_RESOURCE_URL = "https://api.facebook.com/method/stream.publish?uid="+ id + "&message=" + postF + "&format=json";
		request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
		service.signRequest(accessToken, request);
		response = request.send();

		String postID = (String) JSONValue.parse(response.getBody());
		System.out.println(postID);
		
		
		postsByFacebook();

		return postID;
	}

}