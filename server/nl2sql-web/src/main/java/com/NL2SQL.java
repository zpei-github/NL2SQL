package com;

import com.node.Node;
import com.node.NodeFactory;
import com.node.entity.FieldNode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NL2SQL {
	public static void main(String[] args) {
		/**
		NodeFactory nodeFactory = new NodeFactory();
		SpringApplication.run(NL2SQL.class, args);
		 */
		Node field = new FieldNode();
		System.out.println(field.getClass());

	}

}
