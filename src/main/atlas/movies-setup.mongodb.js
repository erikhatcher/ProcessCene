/* global use, db */

use('sample_mflix');

// db.getCollection('movies')
//   .aggregate(
// [
//   {
//     $project: {
//       _id: 0,
//       cast: 1,
//     },
//   },
//   {
//     $unwind: "$cast",
//   },
//   {
//     $group: {
//       _id: "$cast",
//     },
//   },
//   {
//     $sort: {
//       _id: 1,
//     },
//   },
//   {
//     $project: {
//       _id: 0,
//       id: {
//         $concat: ["cast-", "$_id"],
//       },
//       type: "cast",
//       name: "$_id",
//     },
//   },
// ]    
//   ).toArray();

db.getCollection('movies')
    .aggregate(
        [
            {
                $project: {
                    _id: 0,
                    genres: 1,
                },
            },
            {
                $unwind: "$genres",
            },
            {
                $group: {
                    _id: "$genres",
                },
            },
            {
                $sort: {
                    _id: 1,
                },
            },
            {
                $project: {
                    _id: 0,
                    id: {
                        $concat: ["genre-", "$_id"],
                    },
                    type: "genre",
                    name: "$_id",
                },
            },
        ]
    ).toArray();