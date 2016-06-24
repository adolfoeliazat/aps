/*
 * Foundation U
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

require('regenerator-runtime/runtime')
import static 'into-u'

doDyingNoisa(async function() {
    var Pool = require('pg').Pool;

    var config = {
      database: 'aps',
      port: 5432,
      user: 'aps',
      password: 'apssecret',
    };

    var pool = new Pool(config);

    //this initializes a connection pool
    //it will keep idle connections open for a (configurable) 30 seconds
    //and set a limit of 10 (also configurable)
    pool.connect(function(err, client, done) {
      if(err) {
        return console.error('error fetching client from pool', err);
      }
//      client.query('select * from users where email = $1', ['toor'], function(err, result) {
      client.query(`insert into users(email, hash, firstName, lastName) values('fred-apstest@mailinator.com', 'qwe', 'zzz', 'yyyy'`, undefined, function(err, result) {
        //call `done()` to release the client back to the pool
        done();

        if(err) {
          return console.error('error running query', err);
        }
        clog(deepInspect({result}));
      });
    });
    
    clog('OK')
})
