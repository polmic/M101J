// mongo functions :
// load("/Documents/dev/intellij projects/M101J/blog/src/scripts/functions.js");


function showposts() { 
	return db.posts.find().pretty();
}

function showusers() { 
	return db.users.find().pretty();
}

function showsessions() { 
	return db.sessions.find().pretty();
}
