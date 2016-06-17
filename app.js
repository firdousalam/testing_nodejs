var express			= require("express"),
	fs 				= require('fs'),
  	_				= require('underscore'),
  	Combinatorics 	= require('js-combinatorics');


var app 	= express();
app.get('/', function (req, res) {
  res.send('Hello World!');
});
// the simple api to generate all the posiable permutation of six digit ticket of the provided numbers
app.get('/generate_ticket_number', function (req, res) {
  	var array_of_combination = Combinatorics.combination(["1","3","5","6","7","9","11","12","14","15","16","17","19","20","21","22","23","25","26","28","29","30","31","33","34","35","36","37","38"], 6);
  	res.send(array_of_combination.toArray());
});
// api to read all previous winner ticket from text file 
app.get('/read_number_of_previous_winner', function (req, res) {
  	fs.readFile("winner.txt", "utf8", function (error, data) {
  		var winner_numbers = data.split("\n");
        res.send(winner_numbers);
    });
});
// api to provide stats that how many time a use can win if he/she might used this lotto before
app.get('/winning_prediction_of_user', function (req, res) {
  	
  	fs.readFile("winner.txt", "utf8", function (error, data) {
  		var winner_numbers							= data.split("\n"),
  			user_provided_numbers 					= ["3","5","12","20","24","33"],
  			lottery_result_array					= '',
  			total_winning_number					= [],
  			total_number_of_draw_drawn 				= winner_numbers.length,
  			no_of_time_given_one_correct_number   	= 0,
  			no_of_time_given_two_correct_number		= 0,
  			no_of_time_given_three_correct_number	= 0,
  			no_of_time_given_four_correct_number	= 0,
  			no_of_time_given_five_correct_number	= 0,
  			no_of_time_hit_the_jackpot				= 0,
  			details_with_one_correct_number			= [],
  			details_with_two_correct_number			= [],
  			details_with_three_correct_number		= [],
  			details_with_four_correct_number		= [],
  			details_with_five_correct_number		= [],
  			details_with_six_correct_number			= [];

  		//taking all the winning numbers from the winner lottery
        for(i=0;i<winner_numbers.length;i++)
        {
        	var winning_number = [];
        	lottery_result_array = winner_numbers[i].split("\t");
        	if(lottery_result_array.length>0)
        	{
	        	if(lottery_result_array.length>2)
	        	{

	        		for(j=1;j<lottery_result_array.length;j++)
	        		{
	        			if(lottery_result_array[j] !='')
	        			{
	        				winning_number.push(lottery_result_array[j]);
	        			}
	        		}
	        	}
	        	else
	        	{
	        		new_lottery_result_array = winner_numbers[i].split(" ");
	        		for(j=1;j<new_lottery_result_array.length;j++)
	        		{
	        			if(new_lottery_result_array[j] != '')
	        			{
	        				winning_number.push(new_lottery_result_array[j]);
	        			}
	        		}
	        	}
	        	var result_json		={
						        		"date"				: lottery_result_array[0],
						        		"winnig_numbers"	: winning_number
						        	} 	
	        	total_winning_number.push(result_json);
	        }
        }
        for(k=0;k<total_winning_number.length;k++)
        {
        	var winning_numbers=_.intersection(user_provided_numbers,total_winning_number[k].winnig_numbers);
        	if(winning_numbers.length==1)
        	{
        		no_of_time_given_one_correct_number++;
        		details_with_one_correct_number.push({	"provided_number" : user_provided_numbers,
        												"lottery_number"  : total_winning_number[k],
        												"winning_number"  : winning_numbers
        											});
        	}
        	if(winning_numbers.length==2)
        	{
        		no_of_time_given_two_correct_number++;
        		details_with_two_correct_number.push({	"provided_number" : user_provided_numbers,
        												"lottery_number"  : total_winning_number[k],
        												"winning_number"  : winning_numbers
        											});
        	}
        	if(winning_numbers.length==3)
        	{
        		no_of_time_given_three_correct_number++;
        		details_with_three_correct_number.push({	"provided_number" : user_provided_numbers,
        												"lottery_number"  : total_winning_number[k],
        												"winning_number"  : winning_numbers
        											});
        	}
        	if(winning_numbers.length==4)
        	{
        		no_of_time_given_four_correct_number++;
        		details_with_four_correct_number.push({	"provided_number" : user_provided_numbers,
        												"lottery_number"  : total_winning_number[k],
        												"winning_number"  : winning_numbers
        											});
        	}
        	if(winning_numbers.length==5)
        	{
        		no_of_time_given_five_correct_number++;
        		details_with_five_correct_number.push({	"provided_number" : user_provided_numbers,
        												"lottery_number"  : total_winning_number[k],
        												"winning_number"  : winning_numbers
        											});
        	}
        	if(winning_numbers.length==6)
        	{
        		no_of_time_hit_the_jackpot++;
        		details_with_six_correct_number.push({	"provided_number" : user_provided_numbers,
        												"lottery_number"  : total_winning_number[k],
        												"winning_number"  : winning_numbers
        											});
        	}
        }
        var response = {};
        response.loterry_number_provided_by_customer = user_provided_numbers;

        response.no_of_time_hit_one_correct_number	 = no_of_time_given_one_correct_number;
        response.details_of_one_correct_loterry		 = details_with_one_correct_number;

        response.no_of_time_hit_two_correct_number	 = no_of_time_given_two_correct_number;
        response.details_of_two_correct_loterry		 = details_with_two_correct_number;

        response.no_of_time_hit_three_correct_number = no_of_time_given_three_correct_number;
        response.details_of_three_correct_loterry	 = details_with_three_correct_number;

      	response.no_of_time_hit_four_correct_number	 = no_of_time_given_four_correct_number;
        response.details_with_four_correct_number	 = details_with_four_correct_number;

        response.no_of_time_hit_five_correct_number	 = no_of_time_given_five_correct_number;
        response.details_with_five_correct_number	 = details_with_five_correct_number;

        response.no_of_time_hit_the_jackpot			 = no_of_time_hit_the_jackpot;
        response.details_with_six_correct_number	 = details_with_six_correct_number;


        res.send(response);
    });
});

app.listen(3000, function () {
  console.log('Example app listening on port 3000!');
});