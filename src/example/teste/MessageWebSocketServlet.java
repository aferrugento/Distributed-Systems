package example.teste;

import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WsOutbound;

import example.server.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.IOException;

@WebServlet("/messageAndOnline")
public class MessageWebSocketServlet extends WebSocketServlet {

	public MessageWebSocketServlet()
	{
		
	}

	private static  long serialVersionUID = 1L;

	public static  Set<MMessageInbound> connections = 	new CopyOnWriteArraySet<MMessageInbound>();

	protected StreamInbound createWebSocketInbound(String subProtocol,
			HttpServletRequest request) {
		return new MMessageInbound(request);
	}

	public final class MMessageInbound extends MessageInbound {

		private String nickname="notNickname";
		private User user=null;

		public MMessageInbound()
		{
			
			
		}
		
		
		private MMessageInbound(HttpServletRequest request) {
			UserBean userBean = LoginServlet.checkUser(request);
			this.nickname = (this.user=userBean.getUser()).getName();
		}

		protected void onOpen(WsOutbound outbound) {
			connections.add(this);
		}

		protected void onClose(int status) {
			connections.remove(this);
		}

		protected void onTextMessage(CharBuffer message) throws IOException {

		}



		protected void onBinaryMessage(ByteBuffer message) throws IOException {
			throw new UnsupportedOperationException("Binary messages not supported.");
		}
		
		public User getUser()
		{
			return this.user;
		}


		public void send(String msg) {
			System.out.println("Entrei no send da message");
			try {
				CharBuffer buffer = CharBuffer.wrap(msg);
				this.getWsOutbound().writeTextMessage(buffer);
			} catch (IOException ignore) {}
		
	
			
		
	}
}
}