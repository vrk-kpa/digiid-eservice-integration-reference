const express = require('express');
const ensureLoggedIn = require('connect-ensure-login').ensureLoggedIn;
const router = express.Router();

router.get('/', ensureLoggedIn('/'), (req, res) => {
  res.render('users', { user: req.user });
});

module.exports = router;