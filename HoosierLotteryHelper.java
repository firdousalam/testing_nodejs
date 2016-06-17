import java.util.*;
import java.io.*;
import java.util.Random;
import java.util.Arrays;

public class HoosierLotteryHelper{
	File data;//current list of lotto numbers	
	Draw[] draws;//all the draws read from data
	NumberStats[] numberStats;
	GameStats gameStats;
	PredictionStats predictionStats;
	ArrayList<Integer> topDogs;
	ArrayList<Integer> predictions;
	Profile profile;
	int totalDraws, jackpot, jackpotPredictions;
	
	
	HoosierLotteryHelper(){//constructor
		profile = new Profile();
		try{
			data = new File("winners.txt");
		}catch(Exception e){}
		totalDraws = 0;
		jackpot = 0;
		jackpotPredictions = 0;
		gameStats = new GameStats();
		predictionStats = new PredictionStats();
		numberStats = new NumberStats[48];
		for(int i = 0; i < 48; i++)
			numberStats[i] = new NumberStats();
		topDogs = new ArrayList<Integer>();
		predictions = new ArrayList<Integer>();
				
	}//ends constructor
	public static void main(String[] args){//main
		HoosierLotteryHelper p = new HoosierLotteryHelper();
		p.readDrawData();//get most current draw data
		
		/*for(int i = 0; i < 500; i++){
			p.runProfile(p.profile);
			p.profile.reset();
		}*/ 	
		p.gameDrawProcess(5);
		p.getLottoWheel();
		p.runAnalysis(p.totalDraws-1);
		System.out.println("\nI've predicted: " + p.jackpotPredictions + " jackpots!");
		
		
		
	}//ends main
	public void gameDrawProcess(int x){
		getStats(totalDraws);
		printGameStats();
		printNumberStats();
		for(int i = totalDraws - 10 ; i < totalDraws; i++)
			printDraw(draws[i]);
		getPredictions();
		printPredictions();
		printPicks(makePicks(x));
	}
	//random checker 
	public void helper(){
		
		if(totalDraws == (gameStats.OOOOOO + gameStats.OOOOOE + gameStats.OOOOEE + gameStats.OOOEEE + gameStats.OOEEEE + gameStats.OEEEEE + gameStats.EEEEEE))
			System.out.println("Data matches");
	}
	//gets stats uptil whatever drawing in the data. totalDraws is index to use for most recent drawing
	public void getStats(int x){
		for(int i = 0; i < x; i++){
			updateStats(draws[i]);
			//printDraw(draws[i]);
		}			
	}//ends getStats
	public void runAnalysis(int numDraws){
		for(int i = 0; i < numDraws; i++){
			updateStats(draws[i]);
			getPredictions();
			checkPredictions(draws[i+1]);
			
		}//ends for
	}//ends runAnalysis
	public void updateStats(Draw d){
		//Show draw
		//printDraw(d);
		//update total picks
		
	
		gameStats.lastDrawDate = d.date;
			for(int i = 0; i < 6; i++){
			numberStats[d.numbers[i] - 1].totalPicks++;
			numberStats[d.numbers[i] - 1].wasPicked = true;
		}
		//update pairedWith
		for(int i = 0; i < 6; i++){
			for(int j = 0; j < 6; j++){
				numberStats[d.numbers[i] - 1].pairedWith[d.numbers[j] - 1] += 1;
				//System.out.println(d.numbers[i] + " paired with " + d.numbers[j]);
			}
		}
			
		//update last since picked
		for(int i = 0; i < 48; i++){
			if(numberStats[i].wasPicked == true){
				numberStats[i].dataForAverageBetweenPicks += numberStats[i].lastSincePicked;
				numberStats[i].lastSincePicked = 0;
				numberStats[i].wasPicked = false;
			}else
				numberStats[i].lastSincePicked++;
			//update averageBetweenPicks
			numberStats[i].averageBetweenPicks = numberStats[i].getAverageBetweenPicks();
		}
		//update averagePicks for game
		gameStats.getAveragePicks();
		//update odd/even count
		if(d.odd == 0){
			gameStats.EEEEEE++;
			gameStats.last_since_EEEEEE_data += gameStats.last_since_EEEEEE;
			gameStats.last_since_EEEEEE = 0;
		}else
				gameStats.last_since_EEEEEE++;
		if(d.odd == 1){
			gameStats.OEEEEE++;
			gameStats.last_since_OEEEEE_data += gameStats.last_since_OEEEEE;
			gameStats.last_since_OEEEEE = 0;
		}else
			gameStats.last_since_OEEEEE++;
			
		if(d.odd == 2){
			gameStats.OOEEEE++;
			gameStats.last_since_OOEEEE_data += gameStats.last_since_OOEEEE;
			gameStats.last_since_OOEEEE = 0;
		}else
			gameStats.last_since_OOEEEE++;
			
		if(d.odd == 3){
			gameStats.OOOEEE++;
			gameStats.last_since_OOOEEE_data += gameStats.last_since_OOOEEE;
			gameStats.last_since_OOOEEE = 0;
		}else
			gameStats.last_since_OOOEEE++;
			
		if( d.odd == 4){
			gameStats.OOOOEE++;
			gameStats.last_since_OOOOEE_data += gameStats.last_since_OOOOEE;
			gameStats.last_since_OOOOEE = 0;
		}else
			gameStats.last_since_OOOOEE++;

		if(d.odd == 5){
			gameStats.OOOOOE++;
			gameStats.last_since_OOOOOE_data += gameStats.last_since_OOOOOE;
			gameStats.last_since_OOOOOE = 0;
		}else
			gameStats.last_since_OOOOOE++;
		
		if(d.odd == 6){
			gameStats.OOOOOO++;
			gameStats.last_since_OOOOOO_data += gameStats.last_since_OOOOOO;
			gameStats.last_since_OOOOOO = 0;
		}else
			gameStats.last_since_OOOOOO++;
			
		
	}
	public void getPredictions(){
		
		topDogs.clear();
		predictions.clear();
		for(int i = 0; i < 48; i++){
			if(numberStats[i].totalPicks < gameStats.averagePicks && numberStats[i].lastSincePicked > numberStats[i].averageBetweenPicks){
				topDogs.add(i+1);
			}else{
				if(numberStats[i].totalPicks < gameStats.averagePicks)
					predictions.add(i+1);
				if(numberStats[i].lastSincePicked > numberStats[i].averageBetweenPicks)
					predictions.add(i+1);
			}	
		}//ends else
		predictionStats.totalPredictions++;
		
	}//ends predictions
	public void checkPredictions(Draw d){
		int matched = 0;
		for(int i = 0; i < 6; i++){
			if(predictions.contains(d.numbers[i]) || topDogs.contains(d.numbers[i]))
				matched++;
		}
		predictionStats.dataForPredictionAverage += matched;
		predictionStats.predictionAverage = predictionStats.dataForPredictionAverage/predictionStats.totalPredictions;
		/*
		if(matched == 5){
				System.out.println();
				System.out.println("You Predicted 5 numbers!");
				printDraw(d);
				printPredictions();
				
				
			}
		*/
		if(matched == 6){
				System.out.println();
				System.out.println("You predicted the lotto");
				printDraw(d);
				printPredictions();
				jackpotPredictions++;
				
			}
	}
	public Draw[] makePicks(int x){
		profile.totalSpent += x;
		x += profile.freeTickets;
		profile.totalPlayedTickets += x;
		profile.totalFreeTickets += profile.freeTickets;
		profile.freeTickets = 0;
		//System.out.println("Making " + x + " picks.");
		Draw[] tempPicks = new Draw[x];
		ArrayList selections = new ArrayList<Integer>();
		selections.addAll(topDogs);
		selections.addAll(predictions);
		Collections.sort(selections);
		Random r = new Random();
		int index = 0;
		ArrayList temp = new ArrayList<Integer>(selections);
			for(int i = 0; i < x; i++){
				if(temp.size() < 6)
				temp = new ArrayList<Integer>(selections);
				tempPicks[i] = new Draw();
				for(int j = 0; j < 6; j++){
						//System.out.println("Temp Size: " + temp.size());
						index = Math.abs(r.nextInt())%temp.size();
						tempPicks[i].numbers[j] = (int)temp.remove(index);
						if(tempPicks[i].numbers[j] % 2 == 0)
							tempPicks[i].even++;
						else
							tempPicks[i].odd++;
						if(tempPicks[i].numbers[j] > 24)
							tempPicks[i].HR++;
						else
							tempPicks[i].LR++;
				}
				Arrays.sort(tempPicks[i].numbers);
				//prints out the predicted pick
				//printDraw(tempPicks[i]);
				
			}
		return tempPicks;
	}
	public void checkIfWon(Draw[] selected, Draw drawn){
		//System.out.println("Check if won...");
		int matched = 0;
		for(int i = 0; i < selected.length; i++){
			for(int j = 0; j < 6; j++){
				for(int k = 0; k < 6; k++){
					if(selected[i].numbers[j] == drawn.numbers[k]){
						matched++;
					}//ends if	
				}//ends for k
			}//ends for j 
			if(matched == 2){
				//System.out.println("Free Ticket");
				//printDraw(selected[i]);
				profile.freeTickets++;
			}
			if(matched == 3){
				//System.out.println("$3 Winning Ticket");
				//printDraw(selected[i]);
				profile.winnings += 2;
			}
			if(matched == 4){
				//System.out.println("$20 winning ticket");
				//printDraw(selected[i]);
				profile.winnings += 20;
			}
			if(matched == 5){
				//System.out.println("$1000 winning ticket");
				profile.winnings += 500;
			}
			if(matched == 6){
				//System.out.println("You won the lotto!");
				System.out.println("Jackpot Details:");
				printDraw(drawn);
				profile.winnings += 1000000;
			}
				
				
					
			
			matched = 0;
		}//ends for i

		//printDraw(drawn);
		
	}
	public void readDrawData(){
		System.out.println("Reading historical draw data...");
		String cDate;
		int[] cNumbers;
		int odd = 0, even = 0, LR = 0, HR = 0;
		try{
			Scanner c = new Scanner(data);
			while(c.hasNext()){
			c.nextLine();
			totalDraws++;
			}//ends while
			
			Scanner s = new Scanner(data);
			draws = new Draw[totalDraws];
			
			for(int i = 0; i < totalDraws; i++){
				cDate = s.next();
				cNumbers = new int[6];
				for(int n = 0; n < 6; n++){
					cNumbers[n] = s.nextInt();
					if(cNumbers[n] % 2 == 0)
						even++;
					else
						odd++;
					if(cNumbers[n] < 25)
						LR++;
					else
						HR++;
				}	
				draws[i] = new Draw(cDate, cNumbers, odd, even, LR, HR);
				//printDraw(draws[i]);
				odd = 0; even = 0; LR = 0; HR = 0;
				
			}//ends for i
				
		}catch(Exception e){}
		//reverse order of draws to latest is last in array
		Collections.reverse(Arrays.asList(draws));
		System.out.println("Draw data collected.");
	}//ends readDrawData
	public void printDraw(Draw d){
		if(d.date != null)
		System.out.print(d.date + ": ");
		for(int i = 0; i < 6; i++)
				System.out.print(d.numbers[i] + " ");
		System.out.print("Odd:Even - " + d.odd + ":" + d.even + "  LR:HR - " + d.LR + ":" + d.HR);
		System.out.println();
	}
	public void printDrawNumbersOnly(Draw d){
		for(int i = 0; i < 6; i++)
			System.out.print(d.numbers[i] + " " );
		System.out.print("\n");
	}
	public void printAllDrawsAcending(){
		for(int i = 0; i < totalDraws; i++)
			printDraw(draws[i]);
	}
	public void printAllDrawsDecending(){
		for(int i = totalDraws - 1; i >= 0; i--)
			printDraw(draws[i]);
	}
	public void printNumberStats(){
		for(int i = 0; i < 48; i++){
			System.out.println("===========");
			System.out.println((i+1) + " has been picked " + numberStats[i].totalPicks + " times");
			System.out.println("Last picked : " + numberStats[i].lastSincePicked);
			System.out.println("Average between picks: " + numberStats[i].averageBetweenPicks);
			System.out.println("Paired With:");
				for(int j = 0; j < 48; j++)
					System.out.println((j+1) +" paired "+ numberStats[i].pairedWith[j] + " times");
		}
		
	}
	public void printGameStats(){
		System.out.println("==============");
		System.out.println("Average picks: " + gameStats.averagePicks);
		System.out.println("OOOOOO: " + gameStats.OOOOOO + " | " + ((gameStats.OOOOOO/(double)totalDraws)) + " Last Since: " + gameStats.last_since_OOOOOO + " Average Between: " + (gameStats.last_since_OOOOOO_data/gameStats.OOOOOO));
		System.out.println("OEEEEE: " + gameStats.OEEEEE  + " | " + ((gameStats.OEEEEE/(double)totalDraws)) + " Last Since: " + gameStats.last_since_OEEEEE + " Average Between: " + (gameStats.last_since_OEEEEE_data/gameStats.OEEEEE));
		System.out.println("OOEEEE: " + gameStats.OOEEEE + " | " + ((gameStats.OOEEEE/(double)totalDraws)) + " Last Since: " + gameStats.last_since_OOEEEE + " Average Between: " + (gameStats.last_since_OOEEEE_data/gameStats.OOEEEE));
		System.out.println("OOOEEE: " + gameStats.OOOEEE + " | " + ((gameStats.OOOEEE/(double)totalDraws)) + " Last Since: " + gameStats.last_since_OOOEEE + " Average Between: " + (gameStats.last_since_OOOEEE_data/gameStats.OOOEEE));
		System.out.println("OOOOEE: " + gameStats.OOOOEE + " | " + ((gameStats.OOOOEE/(double)totalDraws)) + " Last Since: " + gameStats.last_since_OOOOEE+ " Average Between: " + (gameStats.last_since_OOOOEE_data/gameStats.OOOOEE));
		System.out.println("OOOOOE: " + gameStats.OOOOOE + " | " + ((gameStats.OOOOOE/(double)totalDraws)) + " Last Since: " + gameStats.last_since_OOOOOE + " Average Between: " + (gameStats.last_since_OOOOOE_data/gameStats.OOOOOE));
		System.out.println("EEEEEE: " + gameStats.EEEEEE + " | " + ((gameStats.EEEEEE/(double)totalDraws)) + " Last Since: " + gameStats.last_since_EEEEEE+ " Average Between: " + (gameStats.last_since_EEEEEE_data/gameStats.EEEEEE));
	}
	public void printPredictions(){
		System.out.println("TopDog Set: " + topDogs.size());
		System.out.println(topDogs.toString());
		System.out.println("LowDog Set: " + predictions.size());
		System.out.println(predictions.toString());
		System.out.println("Total Prediction Set: " + (topDogs.size() + predictions.size()));
	}
	public void printPicks(Draw[] x){
		System.out.println("Your picks: ");
		for(int i = 0; i < x.length; i++)
			printDraw(x[i]);
	}
	public void printProfile(Profile p){
		if((p.winnings - p.totalSpent) > 0){
		System.out.println("======================");
		System.out.println("Profile Information");
		System.out.println("Draw budget: " + p.drawBudget);
		System.out.println("Played Tickets: " + p.totalPlayedTickets);
		System.out.println("Total Spent: $" + p.totalSpent);
		System.out.println("Free Tickets: " + p.totalFreeTickets);
		System.out.println("Revenue: $" + p.winnings);
		System.out.println("Profit: $" + (p.winnings - p.totalSpent));
			
		}
		
		
		
	}
	public  int[] buildIntArray(ArrayList<Integer> integers) {
    int[] ints = new int[integers.size()];
    int i = 0;
    for (Integer n : integers) {
        ints[i++] = n;
    }
    return ints;
}
	
	public void getLottoWheel(){
		ArrayList selections = new ArrayList<Integer>();
		selections.addAll(topDogs);
		selections.addAll(predictions);
		Collections.sort(selections);
		int[] set = buildIntArray(selections);
		int[] temp = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48};
		printLottoWheel(set);
	}
	public void printLottoWheel( int[] set){
		int slots = 6;
		int n = set.length;
		int total = 0;
		for(int a = 1; a <= n- slots  + 1; a++)
			for(int b = a + 1; b <= (n - slots + 2); b++)
				for(int c = b + 1; c <= (n - slots + 3); c++)
					for(int d = c + 1; d <= (n - slots + 4); d++)
						for(int e = d + 1; e <= (n - slots + 5); e++)
							for(int f = e + 1; f <= (n - slots + 6); f++){
								System.out.println(set[a-1] + " " + set[b-1] + " " + set[c-1] + " " + set[d-1] + " " + set[e-1] + " " + set[f-1]);
								total++;
							}//ends for f
		System.out.println("Lotto wheel contains " + total + " picks.");
			
		
		
	}
	
	public void runProfile(Profile p){
	
		getStats(p.index);
		for(int i = p.index; i < totalDraws - 1; i++){
			updateStats(draws[i]);
			//System.out.println("Getting predictions for " + draws[i+1].date);
			getPredictions();
			//printPredictions();
			p.profilePicks = makePicks(p.drawBudget);
			//printPicks(p.profilePicks);
			checkIfWon(p.profilePicks, draws[i+1]);
			//System.out.println("==================");
		}
		printProfile(p);
			
			
		
	}
	//Helper classes
	class Profile{
		int drawBudget;
		int totalSpent;
		int winnings;
		int freeTickets;
		int totalFreeTickets;
		int totalPlayedTickets;
		int index;
		Draw[] profilePicks;
		String startDate;//enter in same format as data date
		Profile(){
			this.freeTickets = 0;
			this.totalFreeTickets = 0;
			this.totalSpent = 0;
			this.index = 1;
			this.drawBudget = 100;
			this.profilePicks = new Draw[this.drawBudget];
		}
		public void reset(){
			freeTickets = 0;
			totalFreeTickets = 0;
			totalSpent = 0;
			totalPlayedTickets = 0;
			winnings = 0;
		}
	}
	class Stats{
		GameStats gs;
		NumberStats ns;
		public void getStats(){
			
		}
	}
	class PredictionStats{
		int totalPredictions, predictionAverage, dataForPredictionAverage;
		PredictionStats(){
			totalPredictions = 0;
			predictionAverage = 0;
			dataForPredictionAverage = 0;
		}
	}
	class GameStats{
		String lastDrawDate;
		int totalPicks;
		int averagePicks;
		int OEEEEE, OOEEEE, OOOEEE, OOOOEE, OOOOOE, OOOOOO, EEEEEE;
		int last_since_OEEEEE, last_since_OOEEEE,last_since_OOOEEE,last_since_OOOOEE,last_since_OOOOOE,last_since_OOOOOO,last_since_EEEEEE;
		int last_since_OEEEEE_avg, last_since_OOEEEE_avg, last_since_OOOEEE_avg, last_since_OOOOEE_avg, last_since_OOOOOE_avg, last_since_OOOOOO_avg, last_since_EEEEEE_avg;
		int last_since_OEEEEE_data, last_since_OOEEEE_data, last_since_OOOEEE_data, last_since_OOOOEE_data, last_since_OOOOOE_data, last_since_OOOOOO_data, last_since_EEEEEE_data;
		
		GameStats(){
			this.totalPicks = 0;
			this.OEEEEE = 0; this.OOEEEE = 0; this.OOOEEE = 0; this.OOOOEE = 0; this.OOOOOE = 0; this.OOOOOO = 0; this.EEEEEE = 0;
			
			this.last_since_OEEEEE = 0; this.last_since_OOEEEE = 0; this.last_since_OOOEEE = 0;
			this.last_since_OOOOEE = 0; this.last_since_OOOOOE = 0; this.last_since_OOOOOO = 0; this.last_since_EEEEEE = 0;
			
			this.last_since_OEEEEE_avg = 0; this.last_since_OOEEEE_avg = 0; this.last_since_OOOEEE_avg = 0;
			this.last_since_OOOOEE_avg = 0; this.last_since_OOOOOE_avg = 0; this.last_since_OOOOOO_avg = 0; this.last_since_EEEEEE_avg = 0;
			
			this.last_since_OEEEEE_data = 0; this.last_since_OOEEEE_data = 0; this.last_since_OOOEEE_data = 0;
			this.last_since_OOOOEE_data = 0; this.last_since_OOOOOE_data = 0; this.last_since_OOOOOO_data = 0; this.last_since_EEEEEE_data = 0;
		}
		public void getAveragePicks(){
			int picks = 0;
			for(int i = 0; i < 48; i++)
				picks += numberStats[i].totalPicks;
			this.averagePicks = picks/48;
		}
	}
	class NumberStats{
		int totalPicks;
		int lastSincePicked;
		int averageBetweenPicks;
		int dataForAverageBetweenPicks;
		boolean wasPicked;
		int[] pairedWith;
		NumberStats(){
			this.totalPicks = 0;
			this.lastSincePicked = 0;
			this.averageBetweenPicks = 0;
			this.dataForAverageBetweenPicks = 0;
			this.wasPicked = false;
			this.pairedWith = new int[48];
				for(int i = 0; i < 48; i++)
					this.pairedWith[i] = 0;
			
		}//ends NumberStats()
		public int getAverageBetweenPicks(){
			if(this.totalPicks != 0)
				return this.dataForAverageBetweenPicks/this.totalPicks;
			else
				return 0;
		}
	}
	class Draw{
		String date;
		int[] numbers;
		int odd, even, LR, HR;
		Draw(){
			numbers = new int[6];
			for(int i = 0; i < 6; i++)
				this.numbers[i] = 0;
		}
		Draw(String date, int[] numbers, int odd, int even, int LR, int HR){
			this.date = date;
			this.numbers = numbers;
			this.odd = odd;
			this.even = even;
			this.LR = LR;
			this.HR = HR;
		}//ends Constructor
	}
}//ends class HoosierLotteryHelper