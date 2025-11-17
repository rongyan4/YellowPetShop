var express = require("express");
var router = express.Router();

const homeController = require('../controllers/homeController');

/* GET home page. */
router.get("/", function (req, res, next) {
  res.render("index", { title: "Express" });
});

// API 路由
router.get("/home/swipe", homeController.getSwipe);
router.get("/home/recommend", homeController.getRecommend);

module.exports = router;
