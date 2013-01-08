package example.teste;

import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WsOutbound;

import example.server.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;


import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.io.IOException;

@WebServlet("/chat")
public class ChatWebSocketServlet extends WebSocketServlet {



	private static  long serialVersionUID = 1L;

	private static  Set<ChatMessageInbound> connections = 	new CopyOnWriteArraySet<ChatMessageInbound>();

	protected StreamInbound createWebSocketInbound(String subProtocol,
			HttpServletRequest request) {
		return new ChatMessageInbound(request);
	}

	public final class ChatMessageInbound extends MessageInbound {

		private String nickname="notNickname";
		private User user=null;

		
		
		private ChatMessageInbound(HttpServletRequest request) {
			UserBean userBean = LoginServlet.checkUser(request);
			this.nickname = (this.user=userBean.getUser()).getName();
		}

		protected void onOpen(WsOutbound outbound) {
			connections.add(this);
			broadcast(nickname + " just joined the chatroom!\n");
		}

		protected void onClose(int status) {
			broadcast(nickname + " just left the chatroom.\n");
			connections.remove(this);
		}

		protected void onTextMessage(CharBuffer message) throws IOException {
			// never trust the client
			
			String filteredMessage = filter(message.toString());
			broadcast(nickname + ":  " + filteredMessage);
		}

		public void broadcast(String message) { // send message to all
			
			for (ChatMessageInbound connection : connections) {
				try {
					CharBuffer buffer = CharBuffer.wrap(message);
					connection.getWsOutbound().writeTextMessage(buffer);
				} catch (IOException ignore) {}
			}
		}

		public String filter(String message) {
			if (message == null)
				return (null);
			// filter characters that are sensitive in HTML
			char content[] = new char[message.length()];
			message.getChars(0, message.length(), content, 0);
			StringBuilder result = new StringBuilder(content.length + 50);
			for (int i = 0; i < content.length; i++) {
				switch (content[i]) {
				case '<':
					result.append("&lt;");
					break;
				case '>':
					result.append("&gt;");
					break;
				case '&':
					result.append("&amp;");
					break;
				case '"':
					result.append("&quot;");
					break;
				default:
					result.append(content[i]);
				}
			}
			return (result.toString());
		}

		protected void onBinaryMessage(ByteBuffer message) throws IOException {
			throw new UnsupportedOperationException("Binary messages not supported.");
		}
	}
}