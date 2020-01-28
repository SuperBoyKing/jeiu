package Background;

import java.util.List;

import org.snu.ids.ha.index.Keyword;
import org.snu.ids.ha.index.KeywordExtractor;
import org.snu.ids.ha.index.KeywordList;

/**
 * 
 * @author SuperBoyKing
 * 채팅 형태소 분석 클래스
 */
public class ChatAnalyzer {
	
	KeywordExtractor keyExtractor = new KeywordExtractor();
	
	public void analyze(String email, String chatText) {
		KeywordList keyList = keyExtractor.extractKeyword(chatText, true);
		for (int i = 0; i < keyList.size(); ++i) {
			Keyword kwrd = keyList.get(i);
			System.out.println(kwrd.getString() + "\t" + kwrd.getCnt());
			
		}
		
		/*try {
			MorphemeAnalyzer ma = new MorphemeAnalyzer();
			ma.createLogger(null);
			Timer timer = new Timer();
			timer.start();
			List<MExpression> ret = ma.analyze(string);
			timer.stop();
			timer.printMsg("Time");
			ret = ma.postProcess(ret);
			ret = ma.leaveJustBest(ret);
			List<Sentence> stl = ma.divideToSentences(ret);
			for (int i = 0; i < stl.size(); i++) {
				Sentence st = stl.get(i);
				System.out.println("============================================= " + st.getSentence());
				for (int j = 0; j < st.size(); j++) {
					System.out.println(st.get(j));
				}
			}
			ma.closeLogger();
		} catch(Exception e) {
			e.printStackTrace();
		}*/
	}

}
