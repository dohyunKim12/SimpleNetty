package NettyCh2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class NonBlockingServer {
    private Map<SocketChannel, List<byte[]>> keepDataTrack = new HashMap<>();
    private ByteBuffer buffer = ByteBuffer.allocate(2 * 1024);

    private void startEchoServer(){
        try(
            Selector selector = Selector.open();
            // Java Nio의 selector 생성. 자신에게 등록된 fd에 변경이 있는지 확인하고 변경이 발생한 fd로 접근이 가능하게 해줌.
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            // Non-blocking socket인 server socket channel 생성. (채널이면 논블로킹)(생성 후 바인딩)
         ){
            if((serverSocketChannel.isOpen()) && (selector.isOpen())) {
                serverSocketChannel.configureBlocking(false);
                // Socket Channel의 Default blocking모드는 true. Non-blocking mode를 위해서는이를 false로 변경해 줘야 함.
                serverSocketChannel.bind(new InetSocketAddress(8888));

                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                // server socket channel 객체를 Selector에 등록. Selector가 감지할 이벤트는 OP_ACCEPT(Client와 연결)로 설정.
                System.out.println("Waiting for connect...");

                while(true){
                    selector.select(); // Selector 객체의 select 함수로 fd들에 변경이 발생했는지 검사.
                    // IO event가 발생하지 않으면 여기서 blocking.. 만약 Non-blocking 방식을 택하려면 selectNow() 사용.!!!

                    Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                    // Selector에 등록된 채널 중 IO event가 발생한 채널들의 목록 조회.

                    while(keys.hasNext()){
                        SelectionKey key = (SelectionKey)keys.next();
                        keys.remove(); // 동일한 Event 감지되는 것을 방지하기 위해 key에서 제거(pop같은..)

                        if(!key.isValid()){
                            continue;
                        }

                        if(key.isAcceptable()){
                            this.acceptOP(key, selector); // 연결 요청이면 연결처리 메서드로
                        }
                        else if (key.isReadable()){
                            this.readOP(key); // Data 수신이면 읽기 처리 메서드로
                        }
                        else if (key.isWritable()) {
                            this.writeOP(key); // Data 송신이면 쓰기 처리 메서드로
                        }
                    }
                }
            }
            else{
                System.out.println("Couldn't create Server Socket");
            }
        }
        catch(IOException ex){
            System.err.println(ex);
        }
    }
    private void acceptOP(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        // 연결요청 event가 발생하는 Channel은 무조건 Server socket channel이므로 이벤트가 발생한 channel을 server socket channel로 캐스팅.
        SocketChannel socketChannel = serverChannel.accept();
        // Client 연결 요청을 수락하고 연결된 socket channel을 가져온다.
        socketChannel.configureBlocking(false);
        // Client socket channel을 Non-blocking mode로 설정.

        System.out.println("Client Connected" + socketChannel.getRemoteAddress());

        keepDataTrack.put(socketChannel, new ArrayList<byte[]>());
        socketChannel.register(selector, SelectionKey.OP_READ);
    }
    private  void readOP(SelectionKey key){
        try{
            SocketChannel socketChannel = (SocketChannel) key.channel();
            buffer.clear();
            int numRead = -1;
            try{
                numRead = socketChannel.read(buffer);
            }
            catch(IOException e){
                System.err.println("Error in read data!");
            }

            if(numRead == -1){
                this.keepDataTrack.remove(socketChannel);
                System.out.println("Quit Client connected: " + socketChannel.getRemoteAddress());
                socketChannel.close();
                key.cancel();
                return;
            }
            byte[] data = new byte[numRead];
            System.arraycopy(buffer.array(),0,data,0,numRead);
            System.out.println(new String(data,"UTF-8") + " from " + socketChannel.getRemoteAddress());

            doEchoJob(key, data);
        }catch(IOException ex){
            System.err.println(ex);
        }
    }
    private void writeOP(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        List<byte[]>channelData = keepDataTrack.get(socketChannel);
        Iterator<byte[]> its = channelData.iterator();

        while(its.hasNext()){
            byte[] it = its.next();
            its.remove();
            socketChannel.write(ByteBuffer.wrap(it));
        }
        key.interestOps(SelectionKey.OP_READ);
    }
    private void doEchoJob(SelectionKey key, byte[] data){
        SocketChannel socketChannel = (SocketChannel) key.channel();
        List<byte[]> channelData = keepDataTrack.get(socketChannel);
        channelData.add(data);

        key.interestOps(SelectionKey.OP_WRITE);
    }
    public static void main(String[]args){
    NonBlockingServer main = new NonBlockingServer();
    main.startEchoServer();
    }
}
