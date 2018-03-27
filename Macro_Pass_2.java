import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

public class Macro_Pass_2 {

	Vector <String>PNTAB=new Vector<String>();
	//String[] PNTAB=new String[];
	Vector <String>EVNTAB=new Vector<String>();
	Vector <String>SSNTAB=new Vector<String>();
	//Vector <String>SSTAB=new Vector<String>();
	Integer[][] SSTAB=new Integer[3][2];
	String[][] KPDTAB=new String[3][2];
	String[][] MNT=new String[7][3];
	String[][] MDT=new String[15][2];
	Vector <String> APTAB=new Vector<String>();
	Vector<String> EVTAB=new Vector<String>();

	Integer[] MEC=new Integer[2];
	Integer[] EVTAB_ptr=new Integer[2];
	Integer[] APTAB_ptr=new Integer[2];
	int[] KPDTAB_ptr=new int[2];


	public void pass2(File macro_call,File macro_exp) throws IOException {
		File macro=new File("macro.txt");
		if(!macro.exists())
		{
			macro.createNewFile();
		}

		BufferedReader macroBR=new BufferedReader(new FileReader(macro.getAbsoluteFile()));
		Macro_Pass1 p1=new Macro_Pass1();
		p1.pass1(macroBR);
		macroBR.close();
		PNTAB=p1.PNTAB;
		EVNTAB=p1.EVNTAB;
		SSNTAB=p1.SSNTAB;
		SSTAB=p1.SSTAB;
		KPDTAB=p1.KPDTAB;
		MNT=p1.MNT;
		MDT=p1.MDT;
		KPDTAB_ptr=p1.KPDTAB_ptr;

		BufferedReader macrocBR=new BufferedReader(new FileReader(macro_call.getAbsoluteFile()));
		BufferedWriter macroBW=new BufferedWriter(new FileWriter(macro_exp.getAbsoluteFile(),true));
		String text;
		APTAB.addAll(PNTAB);
		while((text=macrocBR.readLine())!=null) {
			if(text.contains("CLEAR")) {
				int macro_ptr=0;
				for(int i=1;i<3;i++) {
					if(MNT[0][i].equals("CLEAR")){
						macro_ptr=i;
					}
				}
				String[] words=text.split("([\\s,])");
				int i=0;
				if(!words[0].equals("CLEAR")) {
					macroBW.append(words[0]+" ");
					i++;
				}
				if(words[i].equals("CLEAR")) {
					i++;
					MEC[macro_ptr-1]=Integer.parseInt(MNT[4][macro_ptr]);
					EVTAB_ptr[macro_ptr-1]=Integer.parseInt(MNT[3][macro_ptr]);
					EVTAB=EVNTAB;
					APTAB_ptr[macro_ptr-1]=Integer.parseInt(MNT[1][macro_ptr])+Integer.parseInt(MNT[2][macro_ptr]);
					for(int j=0;j<Integer.parseInt(MNT[2][macro_ptr]);j++){
						int q=APTAB.indexOf(KPDTAB[KPDTAB_ptr[macro_ptr-1]-1][0]);
						APTAB.remove(q);
						APTAB.add(q,KPDTAB[0][1]);

					}
					int q=0;
					for(int k=i;k<words.length;k++) {
						APTAB.remove(q);
						APTAB.add(q, words[k]);
						q++;
					}
					while(!MDT[MEC[macro_ptr-1]][1].equals("MEND")){
						if(MDT[MEC[macro_ptr-1]][1].contains("SET")) {					//SET statement
							int s=0,s1=0;
							String[] swords=MDT[MEC[macro_ptr-1]][1].split("([\\s])");
							if(swords[0].contains("E")) {
								s=Integer.parseInt(swords[0].substring(3,swords[0].length()-1));
							}
							if(swords[2].contains("+")) {
								String[] pwords=swords[2].split("([+])");
								for(String p:pwords) {
									if(p.contains("(E")) {
										s1=s1+Integer.parseInt(EVTAB.elementAt(Integer.parseInt(p.substring(3,p.length()-1))-1));
									}else if(p.contains("(P")) {
										s1=s1+Integer.parseInt(APTAB.elementAt(Integer.parseInt(p.substring(3,p.length()-1))-1));
									}else {
										s1=s1+Integer.parseInt(p);
									}
								}
							}else {
								if(swords[2].contains("(E")) {
									s1=s1+Integer.parseInt(EVTAB.elementAt(Integer.parseInt(swords[2].substring(3,swords[2].length()-1))-1));
								}else if(swords[2].contains("(P")) {
									s1=s1+Integer.parseInt(APTAB.elementAt(Integer.parseInt(swords[2].substring(3,swords[2].length()-1))-1));
								}else {
									s1=s1+Integer.parseInt(swords[2]);
								}
							}
							EVTAB.remove(s-1);
							EVTAB.add(s-1,Integer.toString(s1));
							MEC[macro_ptr-1]++;
						}else if(MDT[MEC[macro_ptr-1]][1].contains("AGO")) {						//AGO statement
							macroBW.append(MEC[macro_ptr-1]+"\n");
							String[] swords=MDT[MEC[macro_ptr-1]][1].split("([\\s])");
							int s=0;
							if(swords[1].contains("S")) {
								s=Integer.parseInt(swords[1].substring(3,swords[1].length()-1));
							}
							MEC[macro_ptr-1]=SSTAB[s][1]-1;
							macroBW.append(MEC[macro_ptr-1]+"\n");
						}else if(MDT[MEC[macro_ptr-1]][1].contains("AIF")) {						//AIF statement					
							String[] swords=MDT[MEC[macro_ptr-1]][1].split("([\\s])");
							int s=0;
							if(swords[2].contains("S")) {
								s=Integer.parseInt(swords[2].substring(3,swords[2].length()-1));
								
							}
							int flag=0;
							String[] s2words=swords[1].split("([()])");
							int para1=0,para2=0;
							if(s2words[2].contains("E")) {
								para1=Integer.parseInt(EVTAB.elementAt(Integer.parseInt(s2words[2].substring(2,s2words[2].length()))-1));
							}else if(s2words[2].contains("P")) {
								para1=Integer.parseInt(APTAB.elementAt(Integer.parseInt(s2words[2].substring(2,s2words[2].length()))-1));
							}else {
								para1=Integer.parseInt(s2words[2]);
							}
							
							if(s2words[4].contains("E")) {
								para2=Integer.parseInt(EVTAB.elementAt(Integer.parseInt(s2words[4].substring(2,s2words[4].length()))-1));
							}else if(s2words[4].contains("P")) {
								para2=Integer.parseInt(APTAB.elementAt(Integer.parseInt(s2words[4].substring(2,s2words[4].length()))-1));
							}else {
								para2=Integer.parseInt(s2words[4]);
							}
							
							if(s2words[3].equals("NE") && para1!=para2) {
								flag=1;
							}else if(s2words[3].equals("EQ") && para1==para2) {
								flag=1;
							}else if(s2words[3].equals("LT") && para1<para2) {
								flag=1;
							}else if(s2words[3].equals("LE") && para1<=para2) {
								flag=1;
							}else  if(s2words[3].equals("GT") && para1>para2) {
								flag=1;
							}else if(s2words[3].equals("GE") && para1>=para2) {
								flag=1;
							}
							
							if(flag==1) {
								MEC[macro_ptr-1]=SSTAB[s][1]-1;
							}else {
								MEC[macro_ptr-1]++;
							}
							
						}else {																				//model statement
							String[] swords=MDT[MEC[macro_ptr-1]][1].split("([\\s])");
							int s1=0,s2=0;
							macroBW.append("+ "+swords[0]+" ");
							for(int j=1;j<swords.length;j++) {
								if(swords[j].contains("+")) {
									String[] pwords=swords[2].split("([+])");
									for(String p:pwords) {
										if(p.contains("(E")) {
											String[] s2words=p.split(",");
											if(s2words[0].equals("(E")) {
												s1=Integer.parseInt(s2words[1].substring(0,s2words[1].length()-1));
											}else {
												s1=Integer.parseInt(s2words[2].substring(0,s2words[2].length()-1));
											}
											macroBW.append(EVTAB.elementAt(s1-1)+" ");
										}else if(p.contains("(P")) {
											String[] s2words=p.split(",");
											if(s2words[0].equals("(P")) {
												s2=Integer.parseInt(s2words[1].substring(0,s2words[1].length()-1));
											}else {
												s2=Integer.parseInt(s2words[2].substring(0,s2words[2].length()-1));
											}
											macroBW.append(APTAB.elementAt(s2-1)+" ");
										}else {
											macroBW.append(p+" ");
										}
										macroBW.append("+");
									}
								}else if(swords[j].contains("(E")) {
									s1=Integer.parseInt(swords[j].substring(3,swords[j].length()-1));
									macroBW.append(APTAB.elementAt(s1-1)+" ");
								}else if(swords[j].contains("(P")) {
									s2=Integer.parseInt(swords[j].substring(3,swords[j].length()-1));
									macroBW.append(APTAB.elementAt(s2-1)+" ");
								}else {
									macroBW.append(swords[j]+" ");
								}
							}
							macroBW.append("\n");
							MEC[macro_ptr-1]++;
						}
					}
				}

			}else if(text.contains("CUBE")) {
				int macro_ptr=0;
				for(int i=1;i<3;i++) {
					if(MNT[0][i].equals("CUBE")){
						macro_ptr=i;
					}
				}
				String[] words=text.split("([\\s,])");
				int i=0;
				if(!words[0].equals("CUBE")) {
					macroBW.append(words[0]+" ");
					i++;
				}
				if(words[i].equals("CUBE")) {
					i++;
					MEC[macro_ptr-1]=Integer.parseInt(MNT[4][macro_ptr])-1;
					EVTAB_ptr[macro_ptr-1]=Integer.parseInt(MNT[3][macro_ptr]);
					APTAB_ptr[macro_ptr-1]=Integer.parseInt(MNT[1][macro_ptr])+Integer.parseInt(MNT[2][macro_ptr]);
					for(int j=0;j<Integer.parseInt(MNT[2][macro_ptr]);j++){
						int q=APTAB.indexOf(KPDTAB[KPDTAB_ptr[macro_ptr-1]-1][0]);
						APTAB.remove(q);
						APTAB.add(q,KPDTAB[0][1]);
					}
					int q=Integer.parseInt(MNT[1][macro_ptr-1]);
					for(int k=i;k<words.length;k++) {
						APTAB.remove(q);
						APTAB.add(q, words[k]);
						q++;
					}
					
					while(!MDT[MEC[macro_ptr-1]][1].equals("MEND")){
						if(MDT[MEC[macro_ptr-1]][1].contains("SET")) {					//SET statement
							int s=0,s1=0;
							String[] swords=MDT[MEC[macro_ptr-1]][1].split("([\\s])");
							if(swords[0].contains("E")) {
								s=Integer.parseInt(swords[0].substring(3,swords[0].length()-1));
							}
							if(swords[2].contains("+")) {
								String[] pwords=swords[2].split("([+])");
								for(String p:pwords) {
									if(p.contains("(E")) {
										s1=s1+Integer.parseInt(EVTAB.elementAt(Integer.parseInt(p.substring(3,p.length()-1))-1));
									}else if(p.contains("(P")) {
										s1=s1+Integer.parseInt(APTAB.elementAt(Integer.parseInt(p.substring(3,p.length()-1))-1));
									}else {
										s1=s1+Integer.parseInt(p);
									}
								}
							}else {
								if(swords[2].contains("(E")) {
									s1=s1+Integer.parseInt(EVTAB.elementAt(Integer.parseInt(swords[2].substring(3,swords[2].length()-1))-1));
								}else if(swords[2].contains("(P")) {
									s1=s1+Integer.parseInt(APTAB.elementAt(Integer.parseInt(swords[2].substring(3,swords[2].length()-1))-1));
								}else {
									s1=s1+Integer.parseInt(swords[2]);
								}
							}
							EVTAB.remove(s-1);
							EVTAB.add(s-1,Integer.toString(s1));
							MEC[macro_ptr-1]++;
						}else if(MDT[MEC[macro_ptr-1]][1].contains("AGO")) {
							macroBW.append(MEC[macro_ptr-1]+"\n");
							String[] swords=MDT[MEC[macro_ptr-1]][1].split("([\\s])");
							int s=0;
							if(swords[1].contains("S")) {
								s=Integer.parseInt(swords[1].substring(3,swords[1].length()-1));
							}
							MEC[macro_ptr-1]=SSTAB[s][1]-1;
							macroBW.append(MEC[macro_ptr-1]+"\n");
						}else if(MDT[MEC[macro_ptr-1]][1].contains("AIF")) {
							String[] swords=MDT[MEC[macro_ptr-1]][1].split("([\\s])");
							int s=0;
							if(swords[2].contains("S")) {
								s=Integer.parseInt(swords[2].substring(3,swords[2].length()-1));
								
							}
							int flag=0;
							String[] s2words=swords[1].split("([()])");
							int para1=0,para2=0;
							if(s2words[2].contains("E")) {
								para1=Integer.parseInt(EVTAB.elementAt(Integer.parseInt(s2words[2].substring(2,s2words[2].length()))-1));
							}else if(s2words[2].contains("P")) {
								para1=Integer.parseInt(APTAB.elementAt(Integer.parseInt(s2words[2].substring(2,s2words[2].length()))-1));
							}else {
								para1=Integer.parseInt(s2words[2]);
							}
							
							if(s2words[4].contains("E")) {
								para2=Integer.parseInt(EVTAB.elementAt(Integer.parseInt(s2words[4].substring(2,s2words[4].length()))-1));
							}else if(s2words[4].contains("P")) {
								para2=Integer.parseInt(APTAB.elementAt(Integer.parseInt(s2words[4].substring(2,s2words[4].length()))-1));
							}else {
								para2=Integer.parseInt(s2words[4]);
							}
							
							if(s2words[3].equals("NE") && para1!=para2) {
								flag=1;
							}else if(s2words[3].equals("EQ") && para1==para2) {
								flag=1;
							}else if(s2words[3].equals("LT") && para1<para2) {
								flag=1;
							}else if(s2words[3].equals("LE") && para1<=para2) {
								flag=1;
							}else  if(s2words[3].equals("GT") && para1>para2) {
								flag=1;
							}else if(s2words[3].equals("GE") && para1>=para2) {
								flag=1;
							}
							
							if(flag==1) {
								MEC[macro_ptr-1]=SSTAB[s][1]-1;
							}else {
								MEC[macro_ptr-1]++;
							}
							
						}else {							//model statement
							String[] swords=MDT[MEC[macro_ptr-1]][1].split("([\\s])");
							int s1=0,s2=0;
							macroBW.append("+ "+swords[0]+" ");
							for(int j=1;j<swords.length;j++) {
								if(swords[j].contains("+")) {
									String[] pwords=swords[2].split("([+])");
									for(String p:pwords) {
										if(p.contains("(E")) {
											String[] s2words=p.split(",");
											if(s2words[0].equals("(E")) {
												s1=Integer.parseInt(s2words[1].substring(0,s2words[1].length()-1));
											}else {
												s1=Integer.parseInt(s2words[2].substring(0,s2words[2].length()-1));
											}
											macroBW.append(EVTAB.elementAt(s1-1));
										}else if(p.contains("(P")) {
											String[] s2words=p.split(",");
											if(s2words[0].equals("(P")) {
												s2=Integer.parseInt(s2words[1].substring(0,s2words[1].length()-1));
											}else {
												s2=Integer.parseInt(s2words[2].substring(0,s2words[2].length()-1));
											}
											macroBW.append(APTAB.elementAt(s2-1));
										}else {
											macroBW.append(p);
										}
										//if() {
										macroBW.append("+");
										//}
									}
								}else if(swords[j].contains("(E")) {
									s1=Integer.parseInt(swords[j].substring(3,swords[j].length()-1));
									macroBW.append(APTAB.elementAt(s1-1)+" ");
								}else if(swords[j].contains("(P")) {
									s2=Integer.parseInt(swords[j].substring(3,swords[j].length()-1));
									macroBW.append(APTAB.elementAt(s2-1)+" ");
								}else {
									macroBW.append(swords[j]+" ");
								}
							}
							macroBW.append("\n");
							MEC[macro_ptr-1]++;
						}
					}
				}
			}else {
				macroBW.append(text+"\n");
			}
		}

		macroBW.append("\n APTAB table \n");
		Iterator<String> apitr=APTAB.iterator();
		while(apitr.hasNext()) {
			macroBW.append(apitr.next()+"\n");
		}

		macroBW.append("\n EVTAB table \n");
		Iterator<String> evitr=EVTAB.iterator();
		while(evitr.hasNext()) {
			macroBW.append(evitr.next()+"\n");
		}
		macrocBR.close();
		macroBW.close();
	}


	public static void main(String[] args) throws IOException {

		File macro_call=new File("macroCall.txt");
		if(!macro_call.exists())
		{
			macro_call.createNewFile();
		}

		File macro_exp=new File("macroExpansion.txt");
		if(!macro_exp.exists())
		{
			macro_exp.createNewFile();
		}

		Macro_Pass_2 p2=new Macro_Pass_2();
		p2.pass2(macro_call, macro_exp);


	}

}
