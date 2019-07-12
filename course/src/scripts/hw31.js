
function hw31RemoveTest() {
	cursor = db.students.find();

	while (cursor.hasNext()) {
		print('Another students work : ');
		var oneStudent = cursor.next();
		var oneStudentScores = oneStudent.scores;
		print('scores : ');
		//printjson(oneStudentScores);

		var minScore;
		oneStudentScores.forEach(function(oneScore) {
			if (oneScore.type === 'homework') {
				if (!minScore) {
					print('-> minScore is not set');
					print('-> setting minScore to : ' + oneScore.score);
					minScore = oneScore.score;
				} else {
					if (oneScore.score <= minScore) {
						print('-> second homework score < minScore');
						print('-> homework to remove is the second one');

						db.students.update({_id: oneStudent._id}, {$unset : {"scores.3" : 1 }});
						db.students.update({_id: oneStudent._id}, {$pull : {"scores" : null}});

						print('/!\\ second homework removed');
					} else {
						print('-> second homework score > minScore');
						print('-> homework to remove is the first one');

						db.students.update({_id: oneStudent._id}, {$unset : {"scores.2" : 1 }});
						db.students.update({_id: oneStudent._id}, {$pull : {"scores" : null}});

						print('/!\\ first homework removed');
					}
				}
			}
		});
		minScore = null;
		print('remaining scores : ');
		//printjson(oneStudentScores);
		print('___________________________');
	}

	print('###########################');

	cursor = db.students.find();
	while (cursor.hasNext()) {
		printjson(cursor.next());	
	}

	print('###########################');
}

// VERIFICATION COMMAND : 
// db.students.find( { _id : 137 } ).pretty( )

function hw31Aggregate() { 
	return db.students.aggregate([
		{ '$unwind': '$scores' },
		{ '$group': { '_id': '$_id', 'average': { $avg: '$scores.score' } } },
		{ '$sort': { 'average' : -1 } },
		{ '$limit': 1 } 
		]);
}