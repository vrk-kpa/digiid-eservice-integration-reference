const express = require('express');
const router = express.Router();

router.get('/', (req, res) => {
  res.render('loginSuccess');
});

module.exports = router;