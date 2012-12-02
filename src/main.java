import java.util.*;

import javax.sound.sampled.Line;


public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner scan = new Scanner(System.in);
		Robo robo1 = new Robo();
		String line;
		do{
			line = scan.nextLine();
			System.out.println(robo1.respond(line));
		}while (robo1.getTopic() != 3);
		scan.close();
	}

}