var express = require("express");
var router = express.Router();

const homeController = require('../controllers/homeController');

/* GET home page. */
router.get("/", function (req, res, next) {
  res.render("index", { title: "Express" });
});

router.get("/api/home/swipe", homeController.getSwipe);

module.exports = router;
