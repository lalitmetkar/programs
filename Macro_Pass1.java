import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

public class Macro_Pass1 {
	Vector <String>PNTAB=new Vector<String>();
	//String[] PNTAB=new String[];
	Vector <String>EVNTAB=new Vector<String>();
	Vector <String>SSNTAB=new Vector<String>();
	//Vector <String>SSTAB=new Vector<String>();
	Integer[][] SSTAB=new Integer[3][2];
	String[][] KPDTAB=new String[3][2];
	String[][] MNT=new String[7][3];
	String[][] MDT=new String[15][2];
	int[] SSNTAB_ptr=new int[2];
	int[] SSTAB_ptr=new int[2];
	int[] PNTAB_ptr=new int[2];
	int[] KPDTAB_ptr=new int[2];
	int[] EV_ptr=new int[2];
	int MDTP=1;
	int[] KPDTP=new int[2];
	int[] ss_tot=new int[2];
	int macro_ptr=0;

	public void pass1(BufferedReader macroBR) throws NumberFormatException, IOException {
		MNT[0][0]="Name";
		MNT[1][0]="#PP";
		MNT[2][0]="#KP";
		MNT[3][0]="#EV";
		MNT[4][0]="MDTP";
		MNT[5][0]="KPDTP";
		MNT[6][0]="SSTP";

		SSNTAB_ptr[0]=SSNTAB_ptr[1]=1;
		SSTAB_ptr[0]=SSTAB_ptr[1]=1;
		ss_tot[0]=ss_tot[1]=0;


		String text;
		while((text=macroBR.readLine())!=null){
			if(text.equals("MACRO")){			//start of macro
				text=macroBR.readLine();
				String[] words=text.split("([\\s:,+])");
				macro_ptr++;
				MNT[0][macro_ptr]=words[0];
				for(int i=1;i<words.length;i++){
					String[] parameter=words[i].split("([&])");
					if(parameter[1].contains("=")){
						String[] kpname=parameter[1].split("[=]");
						PNTAB.add(kpname[0]);
						KPDTAB[KPDTAB_ptr[macro_ptr-1]][0]=kpname[0];
						KPDTAB[KPDTAB_ptr[macro_ptr-1]][1]=kpname[1];
						KPDTAB_ptr[macro_ptr-1]++;
					}else{
						PNTAB.add(parameter[1]);
					}

					PNTAB_ptr[macro_ptr-1]++;
				}
				MNT[2][macro_ptr]=Integer.toString(KPDTAB_ptr[macro_ptr-1]);
				MNT[1][macro_ptr]=Integer.toString(PNTAB_ptr[macro_ptr-1]);
				MNT[4][macro_ptr]=Integer.toString(MDTP);
				MNT[6][macro_ptr]=Integer.toString(SSTAB_ptr[macro_ptr-1]);

			}else if(text.equals("MEND")){							//end of macro completed
				//System.out.println(MDTP+" MEND");
				MDT[MDTP-1][0]=Integer.toString(MDTP);
				MDT[MDTP-1][1]="MEND";
				for(int i=0;i<2;i++) {
					if(SSNTAB_ptr[i]==1) {
						MNT[6][i+1]=Integer.toString(0);
					}else {
						SSNTAB_ptr[i]=SSTAB_ptr[i]+SSNTAB_ptr[i]-1;
						MNT[6][i+1]=Integer.toString(SSNTAB_ptr[i]);
					}
					MNT[3][i+1]=Integer.toString(EV_ptr[i]);

					MNT[5][i+1]=Integer.toString(KPDTP[i]);

				}
				MDTP++;

			}else {														//other statements
				String[] words=text.split("([\\s:,])");
				if(words[0].equals("LCL")) {						//LCL statements completed		
					String[] parameter=words[1].split("([&])");
					EVNTAB.add(parameter[1]);
					EV_ptr[macro_ptr-1]++;
					//System.out.println(MDTP+"       "+words[0]+" (E,"+(EVNTAB.indexOf(parameter[1])+1)+") ");
					MDT[MDTP-1][0]=Integer.toString(MDTP);
					MDT[MDTP-1][1]=words[0]+" (E,"+(EVNTAB.indexOf(parameter[1])+1)+") ";
				}else if(words[1].equals("SET")) {					//SET statement completed
					String[] parameter=words[0].split("([&])");
					if(EVNTAB.contains(parameter[1])) {
						//System.out.print(MDTP+" (E,"+(EVNTAB.indexOf(parameter[1])+1)+") "+words[1]+" ");
						MDT[MDTP-1][0]=Integer.toString(MDTP);
						MDT[MDTP-1][1]="(E,"+(EVNTAB.indexOf(parameter[1])+1)+") "+words[1]+" ";
					}
					if(words[2].contains("+")) {
						String[] evwords=words[2].split("([+])");
						for(int i=0;i<evwords.length;i++) {
							//System.out.println(evwords[i]);
							if(evwords[i].contains("&")) {
								String[] ev=words[0].split("([&])");
								if(EVNTAB.contains(ev[1])) {
									//System.out.print("(E,"+(EVNTAB.indexOf(ev[1])+1)+")");
									MDT[MDTP-1][1]=MDT[MDTP-1][1]+"(E,"+(EVNTAB.indexOf(ev[1])+1)+")";
								}
							}else {
								//System.out.print(evwords[i]);
								MDT[MDTP-1][1]=MDT[MDTP-1][1]+evwords[i];
							}
							if(i<evwords.length-1) {
								//System.out.print("+");
								MDT[MDTP-1][1]=MDT[MDTP-1][1]+"+";
							}else {
								//System.out.println();
							}
						}

					}else {
						//System.out.println(words[2]);
						MDT[MDTP-1][1]=MDT[MDTP-1][1]+words[2];
					}

				}else if(words[0].equals("AIF") || words[0].equals("AGO")) {				//AIF and AGO statements
					//System.out.print(MDTP+"       "+words[0]+" ");
					MDT[MDTP-1][0]=Integer.toString(MDTP);
					MDT[MDTP-1][1]=words[0]+" ";
					int i=1;
					while(i<words.length) {
						//System.out.println(words[i]);
						if(words[i].contains(".")) {
							String swords=words[i].substring(1,words[i].length());
							if(SSNTAB.contains(swords)) {
								//System.out.println(" (S,"+SSNTAB.indexOf(swords)+")");
								MDT[MDTP-1][1]=MDT[MDTP-1][1]+" (S,"+(SSNTAB.indexOf(swords)+1)+")";
							}
						}
						else if(words[i].contains("(")) {
							if(words[i].contains("&")) {
								String swords=words[i].substring(2,words[i].length());
								if(EVNTAB.contains(swords)) {
									//System.out.print("((E,"+(EVNTAB.indexOf(swords)+1)+")");
									MDT[MDTP-1][1]=MDT[MDTP-1][1]+"((E,"+(EVNTAB.indexOf(swords)+1)+")";
								}else if(PNTAB.contains(swords)) {
									//System.out.print("((P,"+(PNTAB.indexOf(swords)+1)+")");
									MDT[MDTP-1][1]=MDT[MDTP-1][1]+"((P,"+(PNTAB.indexOf(swords)+1)+")";
								}
							}else {
								//System.out.print(words[i].substring(1,words[i].length()));
								MDT[MDTP-1][1]=MDT[MDTP-1][1]+words[i].substring(1,words[i].length());
							}
						}else if(words[i].contains(")")) {
							if(words[i].contains("&")) {
								String swords=words[i].substring(1,words[i].length()-1);
								//MDT[MDTP-1][1]=MDT[MDTP-1][1]+
								if(EVNTAB.contains(swords)) {
									//System.out.print("(E,"+(EVNTAB.indexOf(swords)+1)+"))");
									MDT[MDTP-1][1]=MDT[MDTP-1][1]+"(E,"+(EVNTAB.indexOf(swords)+1)+"))";
								}else if(PNTAB.contains(swords)) {
									//System.out.print("(P,"+(PNTAB.indexOf(swords)+1)+"))");
									MDT[MDTP-1][1]=MDT[MDTP-1][1]+"(P,"+(PNTAB.indexOf(swords)+1)+"))";
								}
							}else {
								//System.out.print(words[i].substring(0,words[i].length()-1));
								MDT[MDTP-1][1]=MDT[MDTP-1][1]+words[i].substring(0,words[i].length()-1);
							}
						}else {
							//System.out.print(words[i]);
							MDT[MDTP-1][1]=MDT[MDTP-1][1]+words[i];
						}
						i++;
					}
				}else{										//model statements (IS) completed
					int i=0,q;
					if(words[i].contains(".")) {
						String swords=words[i].substring(1,words[i].length());
						if(SSNTAB.contains(swords)) {
							q=SSNTAB.indexOf(swords)+1;
						}else {
							SSNTAB.add(swords);
							q=SSNTAB.indexOf(swords)+1;
							SSNTAB_ptr[macro_ptr-1]++;
							SSTAB_ptr[macro_ptr-1]++;
							SSTAB[Integer.parseInt(MNT[6][macro_ptr])+q-1][0]=Integer.parseInt(MNT[6][macro_ptr])+q-1;
							SSTAB[Integer.parseInt(MNT[6][macro_ptr])+q-1][1]=MDTP;
							ss_tot[macro_ptr]++;
						}
						i++;
					} 
					//System.out.print(MDTP+"       "+words[i]+" ");
					MDT[MDTP-1][0]=Integer.toString(MDTP);
					MDT[MDTP-1][1]=words[i]+" ";
					i++;
					for(int j=i;j<words.length;j++) {
						if(words[j].contains("&")) {
							String[] mwords=words[j].split("([&])");
							for(int k=1;k<mwords.length;k++) {
								//	System.out.println(mwords[k]);
								if(PNTAB.contains(mwords[k])) {
									//System.out.print("(P,"+(PNTAB.indexOf(mwords[k])+1)+")");
									MDT[MDTP-1][1]=MDT[MDTP-1][1]+"(P,"+(PNTAB.indexOf(mwords[k])+1)+")";
								}else if(EVNTAB.contains(mwords[k])) {
									//System.out.print("(E,"+(EVNTAB.indexOf(mwords[k])+1)+")");
									MDT[MDTP-1][1]=MDT[MDTP-1][1]+"(E,"+(EVNTAB.indexOf(mwords[k])+1)+")";
								}else if(mwords[k].contains("+")) {
									String swords=mwords[k].substring(0,mwords[k].length()-1);
									if(EVNTAB.contains(swords)) {
										//System.out.print("(E,"+(EVNTAB.indexOf(swords)+1)+")+");
										MDT[MDTP-1][1]=MDT[MDTP-1][1]+"(E,"+(EVNTAB.indexOf(swords)+1)+")+";
									}else if(PNTAB.contains(swords)) {
										//System.out.print("(P,"+(PNTAB.indexOf(swords)+1)+")+");
										MDT[MDTP-1][1]=MDT[MDTP-1][1]+"(P,"+(PNTAB.indexOf(swords)+1)+")+";
									}

								}
							}
						}else if(words[j].contains(".")) {
							String swords=words[j].substring(1,words[i].length());
							if(SSNTAB.contains(swords)) {
								//System.out.println(" (S,"+SSNTAB.indexOf(swords)+"),");
								MDT[MDTP-1][1]=MDT[MDTP-1][1]+" (S,"+SSNTAB.indexOf(swords)+"),";
							}
						}else {
							//System.out.print(" "+words[j]+",");
							if(words[j].contains("=")) {
								MDT[MDTP-1][1]=MDT[MDTP-1][1]+" "+words[j]+" ";
							}else {
								MDT[MDTP-1][1]=MDT[MDTP-1][1]+words[j]+" ";
							}
						}
					}
					//System.out.println();
				}
				MDTP++;
			}
		}
	}

	public static void main(String[] args) throws IOException {
		File macro=new File("macro.txt");
		if(!macro.exists())
		{
			macro.createNewFile();
		}

		BufferedReader macroBR=new BufferedReader(new FileReader(macro.getAbsoluteFile()));

		Macro_Pass1 p1=new Macro_Pass1();
		p1.pass1(macroBR);


		macroBR.close();

		System.out.println("\n MDT table");
		for(int i=0;i<p1.MDTP-1;i++){
			for(int j=0;j<2;j++){
				System.out.print(p1.MDT[i][j]+" ");
			}
			System.out.println();
		}

		System.out.println("\n MNT table");
		for(int i=0;i<7;i++){
			for(int j=0;j<3;j++){
				System.out.print(p1.MNT[i][j]+" ");
			}
			System.out.println();
		}


		System.out.println("\n PNTAB table");
		Iterator<String> pnitr=p1.PNTAB.iterator();
		while(pnitr.hasNext()) {
			System.out.println(pnitr.next());
		}

		System.out.println("\n KPDTAB table");
		for(int i=0;i<p1.macro_ptr;i++) {
			for(int j=0;j<2;j++) {
				for(int k=0;k<p1.KPDTAB_ptr[i];k++) {
					System.out.print(p1.KPDTAB[i][j]+" ");
				}
			}
			System.out.println();
		}

		System.out.println("\n EVNTAB table");
		Iterator<String> evnitr=p1.EVNTAB.iterator();
		while(evnitr.hasNext()) {
			System.out.println(evnitr.next());
		}

		System.out.println("\n SSNTAB table");
		Iterator<String> ssnitr=p1.SSNTAB.iterator();
		while(ssnitr.hasNext()) {
			System.out.println(ssnitr.next());
		}

		System.out.println("\n SSTAB table");
		for(int i=0;i<p1.macro_ptr;i++) {
			for(int j=0;j<2;j++) {
				for(int k=0;k<p1.ss_tot[i];k++) {
					System.out.print(p1.SSTAB[i][j]+" ");
				}
			}
			System.out.println();
		}

	}
}
