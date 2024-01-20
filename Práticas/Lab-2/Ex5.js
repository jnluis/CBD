prefix = function () {
    var prefixes = db.phones.aggregate([ 
    { $group: { _id: "$components.prefix", totalPrefixes: { $sum: 1 } } }, 
    { $project: { _id: 0, "prefix" :"$_id", totalPrefixes: 1 } }] );

    print(prefixes)
  }

patterns = function() {
    var Numbers = db.phones.find({},{"display": 1, "_id": 0}).toArray();
    var nonRepeatedNumbers = []

    for (var i = 0; i < Numbers.length; i++){
        var n = Numbers[i].display
        n = n.split("-")[1]
        
        var x = []
        var nonRepeating = true

        for( var j = 0; j < n.length; j++){
            if (x.includes(n[j])){
                nonRepeating = false
                break
            }
            x.push(n[j])
        }

        if (nonRepeating){
            nonRepeatedNumbers.push(Numbers[i])
        }
    }

    return nonRepeatedNumbers
}