package service;

class Reply {

    private Message toReply;

    Reply(Message toReply) {
        this.toReply = toReply;
    }

    void send(Messenger messenger, Payload payload) {
        Message replyMessage = new Message(toReply.getId(), toReply.getHeader(MessageHeader.ACTION), payload, MessageType.RESPONSE);
        Address address = new Address(toReply.getHeader(MessageHeader.SOURCE_HOST), toReply.getHeader(MessageHeader.SOURCE_PORT));
        messenger.send(replyMessage, address);
    }

}
