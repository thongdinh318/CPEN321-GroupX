import { getArticleIds } from "./articlesMngt.js";
import { getAllUserHistory } from "../user.js";
  
export async function collaborativeFilteringRecommendations(userId) {
  // Create user-item rating matrix.
  const userInfo = await getAllUserHistory();
  const userItemData = userInfo.userItemData;
  const users = userInfo.users;
  const items = await getArticleIds();

  console.log(userItemData);
  console.log(users);
  console.log(items);

  const userItemMatrix = {};
  for (const interaction of userItemData) {
    if (!userItemMatrix[interaction.userId]) {
      userItemMatrix[interaction.userId] = {};
    }
    if(!userItemMatrix[interaction.userId][interaction.itemId]) {
      userItemMatrix[interaction.userId][interaction.itemId] = interaction.views;
    } else {
      userItemMatrix[interaction.userId][interaction.itemId] += interaction.views;
    }
  }
  
    // Calculate recommendations for the specified user.
    const recommendations = {};
    const userRatings = userItemMatrix[userId];
  
    for (const item of items) {
      if (!userRatings[item]) {
        // Predict the user's rating for the item based on similar users.
        recommendations[item] = predictRating(userId, item, userItemMatrix, users);
      }
    }
  
    // Sort the recommendations by predicted rating in descending order.
    const sortedRecommendations = Object.entries(recommendations).sort((a, b) => b[1] - a[1]);
  
    return sortedRecommendations;
  }
  
function predictRating(userId, itemId, userItemMatrix, users) {
  let numerator = 0;
  let denominator = 0;

  for (const user of users) {
    if (user !== userId && userItemMatrix[user][itemId]) {
      const similarity = calculateSimilarity(userId, user, userItemMatrix);
      numerator += similarity * userItemMatrix[user][itemId];
      denominator += similarity;
    }
  }

  return denominator === 0 ? 0 : numerator / denominator;
}
  
function calculateSimilarity(user1, user2, userItemMatrix) {
  const views1 = userItemMatrix[user1];
  const views2 = userItemMatrix[user2];

  const commonItems = Object.keys(ratings1).filter((item) => views2[item]);

  if (commonItems.length === 0) {
    return 0;
  }

  let sumProduct = 0;
  let sumSquared1 = 0;
  let sumSquared2 = 0;

  for (const item of commonItems) {
    const view1 = views1[item];
    const view2 = views2[item];
    sumProduct += view1 * view2;
    sumSquared1 += view1 ** 2;
    sumSquared2 += view2 ** 2;
  }

  return sumProduct / (Math.sqrt(sumSquared1) * Math.sqrt(sumSquared2));
}

// const userId = 3;
// const recommendations = collaborativeFilteringRecommendations(userId);
// console.log(`Recommendations for user ${userId}:`);
// console.log(recommendations);