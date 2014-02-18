package com.dianping.cat.message.spi.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Test;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.message.internal.DefaultTransaction;
import com.dianping.cat.message.internal.MockMessageBuilder;
import com.dianping.cat.message.spi.MessageTree;
import com.dianping.cat.message.spi.codec.PlainTextMessageCodec;
import com.dianping.cat.message.spi.internal.DefaultMessageTree;

public class PlainTextCodecTest {

	@Test
	public void test() throws InterruptedException {
		MessageTree tree = buildMessages();

		PlainTextMessageCodec codec = new PlainTextMessageCodec();
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer(8192);

		codec.encode(tree, buf);
		int i = buf.readInt(); // get rid of length
		System.out.println(i);

		String str = buf.toString(Charset.forName("utf-8"));

		System.out.println(str);
		MessageTree tree2 = new DefaultMessageTree();
		codec.decode(buf, tree2);
		System.out.println(tree2);

		Thread.sleep(1000);
	}

	public MessageTree buildMessages() {
		Transaction t = Cat.newTransaction("type1", "name1");
		Transaction t2 = Cat.newTransaction("type2", "name2");
		Transaction t3 = Cat.newTransaction("type3", "name3");
		Transaction t4 = Cat.newTransaction("type4", "name4");

		Cat.logEvent("type1\t\n", "name\t\n", "sdfsdf\t\n", convertException(new NullPointerException()));
		Cat.logHeartbeat("type1\t\n", "name\t\n", "sdfsdf\t\n", convertException(new NullPointerException()));

		Cat.logError(new RuntimeException());

		t2.addData(convertException(new NullPointerException()));
		t2.setStatus(convertException(new NullPointerException()));
		t2.complete();
		t3.complete();
		t4.complete();
		MessageTree tree = Cat.getManager().getThreadLocalMessageTree();

		t.setStatus("sfsf\t\n");
		((DefaultTransaction) t).setDurationInMicros(1000);

		System.err.println(tree);
		return tree;
	}

	public static String convertException(Throwable cause) {
		StringWriter writer = new StringWriter(2048);

		cause.printStackTrace(new PrintWriter(writer));

		return writer.toString();
	}

	public MessageTree buildMessage() {
		Message message = new MockMessageBuilder() {
			@Override
			public MessageHolder define() {
				TransactionHolder t = t("URL\t\n", "GET\t\n", 112819).child(
				      t("PigeonCall\t\n",
				            "groupService:groupNoteService_1.0.0:updateNoteDraft(Integer,Integer,String,String)\t\n",
				            "testtest\t\ntest\t\ntest\t\n", 100).child(
				            e("PigeonCall.server\t\n", "10.1.2.99:2011\t\n",
				                  "Execute[34796272]testtest\t\ntest\t\ntest\t\n"))).child(
				      t("PigeonCall\t\n",
				            "groupService:groupNoteService_1.0.1:updateNoteDraft2(Integer,Integer,String,String)\t\n", "",
				            100).child(
				            e("PigeonCall.server\t\n", "10.1.2.199:2011\t\n",
				                  "Execute[34796272]testtest\t\ntest\t\ntest\t\n")));

				return t;
			}
		}.build();

		MessageTree tree = new DefaultMessageTree();
		tree.setDomain("cat test");
		tree.setHostName("test test");
		tree.setIpAddress("test test");
		tree.setThreadGroupName("test test");
		tree.setThreadId("test test");
		tree.setThreadName("test test");
		tree.setMessage(message);
		tree.setMessageId("MobileApi-0a01077f-379304-1362256");
		return tree;
	}
}
